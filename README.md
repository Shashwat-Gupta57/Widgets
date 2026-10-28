# Weather Widget

One of my [desktop widgets](https://dev.talken.in/projects/widgets.html) — this is the **`weather`** branch of [Widgets](https://github.com/Shashwat-Gupta57/Widgets). See the [main branch](https://github.com/Shashwat-Gupta57/Widgets) for all three.


## Weather settings

Copy `weather.properties.example` to `weather.properties` and put in your free [WeatherAPI](https://www.weatherapi.com) key and city. The file is git-ignored, so your key never ends up in the repo.

## Run

These apps need **Java 21 or newer**. If you don't have it, install it once with the [JRE Installer](https://github.com/Shashwat-Gupta57/JRE-Installer/releases/latest) — one click, no admin rights. Every app looks for Java in the place it installs to first, then in `JAVA_HOME`.

Download `Widgets-weather-1.0.0-windows.zip` from the [latest release](https://github.com/Shashwat-Gupta57/Widgets/releases/latest), or from source with the `org.json` jar on the classpath:

```bash
javac *.java
java WeatherWidget
```

## License

[GPL-3.0](LICENSE) © Shashwat Gupta · Flex Studios

Made by [Shashwat Gupta](https://dev.talken.in/) — see more at **[dev.talken.in](https://dev.talken.in/)**.
