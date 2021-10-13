/*
 * Copyright (c) 2008-2016, GigaSpaces Technologies, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gigaspaces.internal.utils.parsers;

import com.j_spaces.jdbc.QueryProcessor;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * @author Kobi
 * @since 10.1
 */
@com.gigaspaces.api.InternalApi
public class LocalDateParser extends AbstractDateTimeParser {
    private final DateTimeFormatter formatter;

    public LocalDateParser() {
        super("java.time.LocalDate", QueryProcessor.getDefaultConfig().getLocalDateFormat());
        this.formatter = DateTimeFormatter.ofPattern(_pattern);
    }

    @Override
    public Object parse(String s) throws SQLException {
        // if the string to parse is not same length as the pattern it will fail, we will try parsing using the default
        // LocalDateParser instead (ISO_LOCAL_DATE)
        try {
            if (s.length() > _pattern.length()) {
                //might be including time
                return LocalDateTime.parse(s.replace(' ', 'T')).toLocalDate();
            } else if (s.length() < _pattern.length()) {
                return LocalDate.parse(s);
            }
        } catch (Exception ignore) {
            //ignore and passthrough to parsing
        }

        try {
            return LocalDate.parse(s, formatter);
        } catch (DateTimeParseException e) {
            throw new SQLException("Wrong " + _desc + " format, expected format=[" + _pattern
                    + "], provided=[" + s + "]", "GSP", -378, e);
        }
    }
}
