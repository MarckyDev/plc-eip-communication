# PLC-Communication

A lightweight, focused toolkit for communicating with industrial PLCs (Programmable Logic Controllers). This project provides simple connection logic to send and receive data from PLCs using the EtherNet/IP (CIP) protocol.

Table of contents
- About
- Features
- Supported protocols
- Requirements
- Installation &amp; build
- Alternative: build/run without Gradle or Maven
- Configuration
- Quick start (example)
- Usage patterns
- Troubleshooting
- Contributing
- Roadmap
- License
- Contact

About
-----
PLC-Communication provides building blocks to connect to PLCs and perform read/write operations in production and test environments. The current implementation focuses on EtherNet/IP (CIP) and provides manual connect/disconnect APIs and read/write operations. Finally, this program is primarily configured for Omron NX-Series PLCs which refers to the tags to its actual symbolic names. For other types of PLCs, please refer to its respective manuals for default values.

Features
--------
- Manual connect and disconnect
- Read and write operations for registers/tags
- EtherNet/IP (CIP) adapter implementation
- Lightweight API suitable for embedding in services or CLI tools

Supported protocols
-------------------
- EtherNet/IP (CIP)

Requirements
------------
- JDK 8+ (ensure JAVA_HOME is set)
- Network access to the target PLC (appropriate firewall rules and routing)
- Build tool (optional): Maven or Gradle (both commands are shown below)

Installation &amp; build
--------------------
Clone the repository:

```bash
git clone https://github.com/MarckyDev/PLC-Communication.git
cd PLC-Communication
```

Build (choose depending on your project setup):
- Maven:
```bash
mvn clean package
```
- Gradle (wrapper):
```bash
./gradlew build
```

Alternative: build/run without Gradle or Maven
---------------------------------------------
If you prefer not to use Gradle or Maven you can compile and run the project directly with the JDK tools, import it into an IDE, or use Docker. Replace package and main-class names below with the actual names from the project.

1) Using javac and jar (Unix/macOS example)
```bash
# compile all Java sources into the out/ directory
mkdir -p out
find src -name '*.java' > sources.txt
javac -d out @sources.txt

# create an executable JAR (replace com.example.Main with your main class)
jar cfe plc-communication.jar com.example.Main -C out .

# run
java -jar plc-communication.jar
```

Windows (PowerShell) example:
```powershell
New-Item -ItemType Directory -Force -Path out
Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName } > sources.txt
javac -d out @sources.txt
jar cfe plc-communication.jar com.example.Main -C out .
java -jar plc-communication.jar
```

2) Using an IDE (recommended for development)
- Import the project into IntelliJ IDEA, Eclipse, or VS Code (Java extension).
- Let the IDE manage compilation and run configurations.
- Run the main class from the IDE.

3) Using Docker
- Create a Dockerfile that uses an OpenJDK image, copies the sources or JAR, and runs the app.
Example Dockerfile (if you have a runnable JAR):
```dockerfile
FROM openjdk:11-jre-slim
COPY plc-communication.jar /app/plc-communication.jar
ENTRYPOINT ["java", "-jar", "/app/plc-communication.jar"]
```
Build and run:
```bash
docker build -t plc-communication .
docker run --network host plc-communication
```

Note: That must be configured to accept EtherNet/IP client connections. This program acts as a client.

Quick start (example)
---------------------
A minimal Java-style example showing the basic flow (adapt to the actual API in the repo):

```java
// Pseudocode - adapt to actual classes/methods in the repo
EtherNetIpClient client = new EtherNetIpClient("192.168.0.100", 44818, 5000);

try {
    client.connect();

    // Read a tag
    Object temperature = client.read("Program:Main.Temp");
    System.out.println("Temp = " + temperature);

    // Write a tag
    client.write("Program:Main.Command", 1);

    // Polling example - schedule periodic reads
    client.poll(Arrays.asList("Program:Main.Temp", "Program:Main.Pressure"), 1000, values -> {
        System.out.println("Polled values: " + values);
    });

} finally {
    client.disconnect();
}
```

- Use polling utilities to schedule periodic reads and callbacks.

Usage patterns
--------------
As a library:
- Instantiate adapter/client with host/port/timeouts.
- Use read/write APIs to interact with tags or registers.
- Use polling utilities to schedule periodic reads and callbacks.

As an agent/service:
- Run as a long-lived process that polls PLCs and forwards data to REST, MQTT, or a database.
- Use systemd, Docker, or container orchestration for production deployments.

Troubleshooting
---------------
- Connection refused: ensure PLC IP and EtherNet/IP port are reachable and not blocked by a firewall.
- Timeouts: increase timeout settings or check network latency and routing.
- Incorrect tag names/types: verify tag/register mapping and data types in the PLC program.
- Handshake failures: enable debug logging to inspect CIP session establishment and error codes.

Contributing
------------
Contributions are welcome:
1. Open an issue to discuss major changes or feature requests.
2. Create a feature branch and open a pull request with a clear description.
3. Add or update tests for new behavior.
4. Follow the repository's code style and commit message conventions.

Consider adding a CONTRIBUTING.md with PR and branching guidelines.

Roadmap
-------
- Maintain and improve EtherNet/IP support and stability.
- Add additional adapters (Modbus, OPC UA) in future releases (TBD).

License
-------
This project is licensed under the MIT License. Please ensure a LICENSE file is present at the repository root.

Contact
-------
Maintainer: MarckyDev  
Repository: https://github.com/MarckyDev/PLC-Communication

Acknowledgements
----------------
Thanks to the open-source projects and protocol libraries that make PLC integration possible. See adapter source files for third-party license notices.
