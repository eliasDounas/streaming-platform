package com.stream.stream_service.enums;

public enum StreamCategory {
    GAMING("Gaming"),
    JUST_CHATTING("Just Chatting"),
    CREATIVE("Art & Creative"),
    SPORTS("Sports"),
    TRAVEL_AND_OUTDOORS("Travel & Outdoors"),
    FOOD_AND_DRINK("Food & Drink"),
    FITNESS_AND_HEALTH("Fitness & Health"),
    SCIENCE_AND_TECHNOLOGY("Science & Technology"),
    EDUCATIONAL("Educational"),
    PODCAST("Podcast"),
    TALK_SHOWS("Talk Shows"),
    ESPORTS("Esports"),
    POLITICS("Politics"),
    ASMR("ASMR"),
    VARIETY("Variety"),
    OTHER("Other");

    private final String displayName;

    StreamCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
