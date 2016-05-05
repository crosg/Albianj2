java -Djava.ext.dirs=E:\sync\tencent\github\albianj\Albianj-All-jars\jars -jar E:\sync\tencent\github\albianj\Albianj-All-jars\pack\Albianj.Packing.jar E:\sync\tencent\github\albianj\Albianj-All-jars\impl
echo "packing is over ,then copy..."
copy /y E:\sync\tencent\github\albianj\Albianj-All-jars\impl\Albianj.spx E:\sync\tencent\github\albianj\Albianj-All-jars\jars
echo "copy end"
PAUSE