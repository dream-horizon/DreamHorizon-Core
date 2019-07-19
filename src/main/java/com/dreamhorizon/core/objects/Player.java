/*
 * DreamHorizonCore
 * Copyright (C) 2019 Dream Horizon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.dreamhorizon.core.objects;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.DbName;
import org.javalite.activejdbc.annotations.Table;

import java.util.UUID;

/**
 * @author Lukas Mansour
 * @since 1.0
 */
@Table("PLAYERS")
@DbName("DreamHorizonCore")
public class Player extends Model {
    public UUID getUUID() {
        return UUID.fromString((String) get("uuid"));
    }
    
    public void setUUID(UUID uuid) {
        set("uuid", uuid.toString());
    }
}
