#!/bin/bash
echo "\`\`\`" > datatree.md
tree src/main/resources/data >> datatree.md
echo "\`\`\`" >> datatree.md

echo "\`\`\`" > assettree.md
tree src/main/resources/assets >> assettree.md
echo "\`\`\`" >> assettree.md