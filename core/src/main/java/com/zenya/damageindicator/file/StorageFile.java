/*
 *     Damage Indicator
 *     Copyright (C) 2021  Zenya
 *     Copyright (C) 2021-2022  Pierce Thompson
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.zenya.damageindicator.file;


import com.zenya.damageindicator.DamageIndicator;

import java.io.File;

public abstract class StorageFile {
    public String directory;
    public String fileName;
    public Integer fileVersion;
    public boolean resetFile;
    public File file;

    public StorageFile(String fileName) {
        this(DamageIndicator.INSTANCE.getDataFolder().getPath(), fileName);
    }

    public StorageFile(String directory, String fileName) {
        this(directory, fileName, null, false);
    }

    public StorageFile(String directory, String fileName, Integer fileVersion, boolean resetFile) {
        this.directory = directory;
        this.fileName = fileName;
        this.fileVersion = fileVersion;
        this.resetFile = resetFile;
        this.file = new File(directory, fileName);
    }
}
