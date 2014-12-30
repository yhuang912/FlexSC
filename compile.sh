cd SimpleBinaryReader
ant dist || exit 1
cd ..

CP="bin:lib/*:SimpleBinaryReader/dist/*"

mkdir -p bin
find . -name "*.java" | grep -v SimpleBinaryReader > source.txt;
javac -cp "$CP" -d bin @source.txt;
rm source.txt
