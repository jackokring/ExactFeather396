package uk.co.kring.ef396.data.backend;

import uk.co.kring.ef396.data.Data;
import uk.co.kring.ef396.data.FilePipe;
import uk.co.kring.ef396.data.streams.LocalDataStream;
import uk.co.kring.ef396.data.streams.TypedStream;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Scanner;
import java.util.zip.DataFormatException;

import static uk.co.kring.ef396.data.FilePipe.*;

public class ImagePBM {

    //PBM - BW
    //PGM - Grey
    //PPM - Colour
    //PAM - Generic spec BW to Colour
    //PNM - Any but not generic

    //bmptoppm/ppmtobmp - bmp conversion (always colour)
    //jbigtopbm/pbmtojbig - jbig conversion? (is BW always so needs up conversion)
    //pnmtofiasco/fiascotopnm - wfa fractal conversion (needs post to ppm? ppm subset of pnm)
    //pgmtoppm - any to colour (up conversion)

    public static InputStream convert(String command, InputStream in) throws IOException {
        PipedOutputStream pos = new PipedOutputStream();
        FilePipe.Task t = new FilePipe.Task() {
            @Override
            public void run() {
                try {
                    Data.execute(command, in, pos, false, true);
                } catch(IOException e) {
                    setError(e);
                }
            }
        };
        t.start();
        return new PipedInputStream(pos);
    }

    public static int[] scanInput(String in) {
        String[] val = in.split("\\s");
        int[] x = new int[val.length];
        for (int i = 0; i < val.length; i++) {
            x[i] = Integer.parseInt(val[i].trim());
        }
        return x;
    }

    public static BufferedImage convertImage(String command, InputStream in) throws IOException {
        in = convert(command + "| pnmgamma -ungamma -cieramp | pnmgamma -srgbramp", in);
        //parse header
        if(in.read() != 'P') Data.io(new DataFormatException("Invalid ppm data magic"));
        Scanner sc = new Scanner(in);
        String line;
        int state = 0;
        int x = 0, y = 0, max = 0;
        int sx = 0, sy = 0;
        int r = 0, g = 0;
        BufferedImage image = null;
        switch(in.read()) {
            case '6':
                boolean binary = false;
                boolean pair = false;
                while(sc.hasNextLine() && !binary) {
                    line = sc.nextLine();
                    if(line.charAt(0) == '#') continue;
                    int[] v = scanInput(line);
                    for(int i: v) {
                        if (state == 0) {
                            x = i;
                            state++;
                            continue;
                        }
                        if(state == 1) {
                            y = i;
                            state++;
                            continue;
                        }
                        if(state == 2) {
                            max = i;
                            if(max > 65536 || max < 1) Data.io(
                                    new DataFormatException("Invalid ppm data scaling"));
                            if(max > 255) pair = true;
                            state++;
                            image = new BufferedImage(x, y, BufferedImage.TYPE_4BYTE_ABGR);
                            binary = true;//escape
                            break;
                        }
                    }
                }
                int j = 0;
                while ((j = in.read()) != -1) {
                    if(state == 3) {
                        r = j;
                        if(pair) r = (r << 8) + in.read();
                        state++;
                        continue;
                    }
                    if(state == 4) {
                        g = j;
                        if(pair) g = (g << 8) + in.read();
                        state++;
                        continue;
                    }
                    int b = j;
                    if(pair) b = (b << 8) + in.read();
                    Color col = new Color(r * 255 / max, g * 255 / max, b * 255 / max);
                    image.setRGB(sx++, sy, col.getRGB());
                    if(sx >= x) {
                        sx = 0;
                        sy++;
                        if(sy >= y) {
                            return image;
                        }
                    }
                    state = 3;//red again
                }
                break;
            case '3':
                while(sc.hasNextLine()) {
                    line = sc.nextLine();
                    if(line.charAt(0) == '#') continue;
                    int[] v = scanInput(line);
                    for(int i: v) {
                        if (state == 0) {
                            x = i;
                            state++;
                            continue;
                        }
                        if(state == 1) {
                            y = i;
                            state++;
                            continue;
                        }
                        if(state == 2) {
                            max = i;
                            if(max > 65536 || max < 1) Data.io(
                                    new DataFormatException("Invalid ppm data scaling"));
                            state++;
                            image = new BufferedImage(x, y, BufferedImage.TYPE_4BYTE_ABGR);
                            continue;
                        }
                        if(state == 3) {
                            r = i;
                            state++;
                            continue;
                        }
                        if(state == 4) {
                            g = i;
                            state++;
                            continue;
                        }
                        Color col = new Color(r * 255 / max, g * 255 / max, i * 255 / max);
                        image.setRGB(sx++, sy, col.getRGB());
                        if(sx >= x) {
                            sx = 0;
                            sy++;
                            if(sy >= y) {
                                return image;
                            }
                        }
                        state = 3;//red again
                    }
                }
                break;
            default: Data.io(new DataFormatException("Invalid ppm data magic"));
        }
        return null;
    }

    public static void convertImage(String command, OutputStream out, BufferedImage image)
            throws IOException {
        PipedOutputStream pos = new PipedOutputStream();
        LocalDataStream.Output dos = new LocalDataStream.Output(pos);
        FilePipe.Task t = new FilePipe.Task() {
            @Override
            public void run() {
                try {
                    dos.writeChars("P6\n");
                    dos.writeChars(image.getWidth() + "\n");
                    dos.writeChars(image.getHeight() + "\n");
                    dos.writeChars("255\n");//max value
                    for (int y = 0; y < image.getHeight(); y++) {
                        for (int x = 0; x < image.getWidth(); x++) {
                            Color col = new Color(image.getRGB(x, y));
                            dos.writeByte(col.getRed());
                            dos.writeByte(col.getBlue());
                            dos.writeByte(col.getGreen());
                        }
                    }
                } catch(IOException e) {
                    setError(e);
                }
            }
        };
        t.start();
        Data.execute("pnmgamma -ungamma -srgbramp | pnmgamma -cieramp | " + command,
                new PipedInputStream(pos), out, true, false);
    }

    public static Object getBMP(TypedStream.Input in) throws IOException {
        return convertImage("bmptoppm", in);
    }

    public static void putBMP(TypedStream.Output out, Object image) throws IOException {
        convertImage("ppmtobmp", out, (BufferedImage) image);
    }

    public static Object getFIASCO(TypedStream.Input in) throws IOException {
        return convertImage("fiascotopnm", in);
    }

    public static void putFIASCO(TypedStream.Output out, Object image) throws IOException {
        convertImage("pnmtofiasco", out, (BufferedImage) image);
    }

    public static void registerBMPComponent(FilePipe fp) {
        registerInputComponent(fp, ImagePBM::getBMP);
        registerInputMangler(fp, FilePipe::getImage);
        registerOutputComponent(fp, ImagePBM::putBMP);
        registerOutputMangler(fp, FilePipe::putImage);
    }

    public static void registerWFAComponent(FilePipe fp) {
        registerInputComponent(fp, ImagePBM::getFIASCO);
        registerInputMangler(fp, FilePipe::getImage);
        registerOutputComponent(fp, ImagePBM::putFIASCO);
        registerOutputMangler(fp, FilePipe::putImage);
    }
}
