<div align="center">

<img src="https://dev.talken.in/assets/img/icons/mix.png" width="96" alt="Widgets logo">

# Widgets

**Three desktop widgets — weather, a transparent clock, and both at once.**

[Project page](https://dev.talken.in/projects/widgets.html) · [Download](https://github.com/Shashwat-Gupta57/Widgets/releases/latest) · [All my projects](https://dev.talken.in/#work)

**3** widgets · **3** branches, one repo · **5 min** weather refresh

</div>

---

<img src="https://dev.talken.in/assets/img/shots/mix-1.webp" alt="MixWidget — time, date and live weather on one panel" width="100%">
<p align="center"><sub>MixWidget — time, date and live weather on one panel</sub></p>

<img src="https://dev.talken.in/assets/img/shots/weather-1.webp" alt="Weather Widget — conditions, temperature, wind, pressure, humidity" width="100%">
<p align="center"><sub>Weather Widget — conditions, temperature, wind, pressure, humidity</sub></p>

<img src="https://dev.talken.in/assets/img/shots/clock-1.webp" alt="Clock Widget — just the digits, floating on the wallpaper" width="100%">
<p align="center"><sub>Clock Widget — just the digits, floating on the wallpaper</sub></p>

## About

Windows took its desktop gadgets away years ago. These bring them back: small, borderless, translucent windows that sit on the wallpaper and stay out of your way — each one draggable, and each one remembering where you left it.

There are three, each on its own branch of the repo: a Weather Widget that pulls live conditions from WeatherAPI, a transparent Clock Widget with a control panel to recolour, move and resize it, and MixWidget, which puts the time, the date and the weather on one panel with its own weather art.

## The widgets

| Widget | Branch | Download |
|---|---|---|
| **Weather Widget** | [`weather`](https://github.com/Shashwat-Gupta57/Widgets/tree/weather) | `Widgets-weather-1.0.0-windows.zip` |
| **Clock Widget** | [`clock`](https://github.com/Shashwat-Gupta57/Widgets/tree/clock) | `Widgets-clock-1.0.0-windows.zip` |
| **MixWidget** | [`mix`](https://github.com/Shashwat-Gupta57/Widgets/tree/mix) | `Widgets-mix-1.0.0-windows.zip` |

## What they do

- **Weather Widget** — Live conditions, temperature, wind speed and direction, pressure and humidity — refreshed every five minutes.
- **Clock Widget** — A transparent clock with a control panel: recolour it, move it, resize it, add or remove it. Plus 24-hour and JavaFX versions.
- **MixWidget** — Time, date and weather together, with its own illustrations for sun, cloud, rain, fog, snow, dust and storms.
- **Borderless & translucent** — Rounded, see-through panels with no window chrome.
- **Drag anywhere** — Each widget remembers its position between launches.
- **Your own key** — The WeatherAPI key and city live in weather.properties beside the app — never in the code.

## Download & run

1. These apps need **Java 21 or newer**. If you don't have it, install it once with the [JRE Installer](https://github.com/Shashwat-Gupta57/JRE-Installer/releases/latest) — one click, no admin rights. Every app looks for Java in the place it installs to first, then in `JAVA_HOME`.
2. Download a widget's zip from the [latest release](https://github.com/Shashwat-Gupta57/Widgets/releases/latest), unzip it and run its `.exe`.
3. Drag a widget wherever you like — it remembers its position.

The Weather and Mix widgets read your [WeatherAPI](https://www.weatherapi.com) key and city from `weather.properties` beside the exe:

```properties
apiKey=YOUR_FREE_WEATHERAPI_KEY
city=Prayagraj
```

## Get the source

```bash
git clone -b weather https://github.com/Shashwat-Gupta57/Widgets
git clone -b clock   https://github.com/Shashwat-Gupta57/Widgets
git clone -b mix     https://github.com/Shashwat-Gupta57/Widgets
```

## Under the hood

| | |
|---|---|
| UI | Java Swing, custom rounded panels, undecorated windows |
| Weather | WeatherAPI over HTTP, parsed with org.json |
| Settings | widget.properties (position) · weather.properties (key, city) |
| Repo | One repo, a branch per widget |

## License

[GPL-3.0](LICENSE) © Shashwat Gupta · Flex Studios

Made by [Shashwat Gupta](https://dev.talken.in/) — see more at **[dev.talken.in](https://dev.talken.in/)**.
