java -Djava.ext.dirs=E:\sync\tencent\java\dev\albianJars2.0\jars -jar E:\sync\tencent\java\dev\albianJars2.0\pack\Albianj.Packing.jar E:\sync\tencent\java\dev\albianJars2.0\impl
echo "packing is over ,then copy..."
copy /y E:\sync\tencent\java\dev\albianJars2.0\impl\Albianj.spx E:\sync\tencent\java\dev\albianJars2.0\jars
echo "copy end"
PAUSE