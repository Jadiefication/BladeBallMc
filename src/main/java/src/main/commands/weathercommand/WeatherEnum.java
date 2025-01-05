package src.main.commands.weathercommand;

import net.minestom.server.instance.Weather;

public enum WeatherEnum {

    CLEAR(Weather.CLEAR),
    RAIN(Weather.RAIN),
    THUNDER(Weather.THUNDER);

    private Weather weather;

    WeatherEnum(Weather weather) {
        this.weather = weather;
    }

    public Weather getWeather() {
        return this.weather;
    }
}
