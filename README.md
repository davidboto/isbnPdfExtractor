### What is this about?

The main goal is to provide a .jar that extracts ISBN information from PDF files. 

The solution applied is yet very naive and feel free to improve/tweak as you like.

### Getting Started

1. Clone this repository
```shell
git clone git@github.com:davidboto/SinacorPDFParser.git
```

2. Build
```shell
$ gradle shadowJar
```

3. Execute
```shell
$ java -jar build/lib/isbnPdfExtractor-all-jar --directory <path/to/pdfs>
```

