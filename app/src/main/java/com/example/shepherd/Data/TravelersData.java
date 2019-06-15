package com.example.shepherd.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TravelersData {

        /**
         * An array of guides.
         */
        public static final List<com.example.shepherd.Data.TravelersData.TravelerItem> TRAVELERS = new ArrayList<TravelerItem>();

        /**
         * A map of  guides data items, by ID.
         */
public static final Map<String, com.example.shepherd.Data.TravelersData.TravelerItem> TRAVELER_MAP = new HashMap<String, com.example.shepherd.Data.TravelersData.TravelerItem>();

        private static final int COUNT = 25;

        static {
            // Add some sample items.
            for (int i = 1; i <= COUNT; i++) {
                TRAVELERS.add( createTravelerItem( i ) );
            }
        }

        private static void addItem(com.example.shepherd.Data.TravelersData.TravelerItem item) {
            TRAVELERS.add( item );
            TRAVELER_MAP.put( item.id, item );
        }
        private static com.example.shepherd.Data.TravelersData.TravelerItem createTravelerItem(int position){
            return new com.example.shepherd.Data.TravelersData.TravelerItem( String.valueOf( position ), "Guide " + position, makeDetails( position ) );
        }

        private static String makeDetails(int position) {
            StringBuilder builder = new StringBuilder();
            builder.append( "Details about guide: " ).append( position );
            for (int i = 0; i < position; i++) {
                builder.append( "\nMore details information here." );
            }
            return builder.toString();
        }

        public static class TravelerItem {
            public final String id;
            public final String content;
            public final String details;

            public TravelerItem(String id, String content, String details) {
                this.id = id;
                this.content = content;
                this.details = details;
            }

            @Override
            public String toString() {
                return content;
            }
        }
    }


