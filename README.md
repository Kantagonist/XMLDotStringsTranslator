# XmlAndDotStringsTransporter
A cmd-line tool for transporting content between Android and iOS Strings resource files automatically.

## Introduction
This project is for a small console tool, which transports string resources between Android and iOS.<br>
I used it to further my pure kotlin knowledge outside the context of Android.<br>
With this tool, you can mix and match resources from each platform and transport them to both.
Input files are mapped together and output into each of the target files.

## Installing
Available on systems: 
- macOS

### Prerequisites
Dependencies:
- Java 11+

The recommended way is to install sdkman and download OpenJdk. 
Simply run the following commands in order:<br>
```
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 11.0.2-open
```

### Easy Install
In order to run the program, you need to download the jar via the [releases tab of this project's GitHub](https://github.com/Kantagonist/XMLDotStringsTranslator/releases).
Afterwards, run the jar via java console command <br>
```
java -jar xdst.jar
```

### Build From Source
If you wish to build the program from source, you need to download the repo.
Then, navigate to the projects repo and run the build command:<br>
```
./gradlew jar
```
Afterwards run the program via
```
java -jar /build/libs/xdst.jar
```

## Usage
The program relies on the existence of a properly formatted `XMLDotStringConfig.yaml` file.
You may choose to name yours differently or put it into a different folder than the jar.
In that case, you need to set the `--config` flag during evocation.

### The Config File
To run the program, create a YAML file which sets the translations.
A sample file has been added in the folder `/src/test/resources/integrationTestSet` for reference.<br>
A basic config file looks like e.g.<br>
```
rootPath: "."
translations:
  - from:
     "Hello.xml"
     "resource/World.strings"
    to:
     "target/myTarget.strings"
  - from:
      "Hello.xml"
    to:
      "target/myTarget.strings
      "target/myXmlTarget.xml"
```
In path `/usr/lib/XMLDotStringConfig.yaml`
The `rootPath` element sets the path relative to the folder of the YAML file.
So, in this example, the root path is set to `/user/lib/`.
The paths in the `translations` section are relative to the root path.<br>
In the first translation, we are referencing the files:
- /usr/lib/Hello.xml
- /usr/lib/resource/World.strings
- /usr/lib/target/myTarget.strings

### Translate
Once your config file is set up, simply execute the jar via:
```
java -jar xdst.jar
```
In case you put your YAML file in a different folder or named it differently, set the config flag via:
```
java -jar xdst.jar --config /absolute/path/to/your/config.yaml
```
or
```
java -jar xdst.jar --config relative/path/to/your/config.yaml
```

## Further Information

### Licensing
This project is licensed under the GNU General Public License v3.
A copy is provided in this project in the file LICENSE.txt.