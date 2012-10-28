/*
 * IzPack - Copyright 2001-2012 Julien Ponge, All Rights Reserved.
 *
 * http://izpack.org/
 * http://izpack.codehaus.org/
 * Copyright 2012 Tim Anderson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.izforge.izpack.panels.userinput.field;

import java.util.List;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.data.binding.OsModel;


/**
 * A field with a number of pre-defined choices.
 *
 * @author Tim Anderson
 */
public abstract class ChoiceField<T extends Choice> extends Field
{
    /**
     * The available choices.
     */
    private final List<T> choices;

    /**
     * The selected index.
     */
    private final int selected;

    /**
     * Constructs a {@code ChoiceField}.
     *
     * @param reader      the reader to get field information from
     * @param installData the installation data
     */
    public ChoiceField(ChoiceFieldReader<T> reader, InstallData installData)
    {
        super(reader, installData);
        this.choices = reader.getChoices();
        this.selected = reader.getSelectedIndex();
    }

    /**
     * Constructs a {@code ChoiceField}.
     *
     * @param variable    the variable associated with the field
     * @param choices     the choices
     * @param selected    The selected choice, or {@code -1} if no choice is selected
     * @param packs       the packs that the field is associated with. If {@code null} or empty,
     *                    indicates the field applies to all packs
     * @param models      the operating systems that the field applies to. If {@code null} or empty, indicates it
     *                    applies to all operating systems
     * @param label       the field label. May be {@code null}
     * @param description the field description. May be {@code null}
     * @param installData the installation data
     */
    public ChoiceField(String variable, List<T> choices, int selected, List<String> packs,
                       List<OsModel> models, String label, String description, InstallData installData)
    {
        super(variable, null, 0, packs, models, null, null, label, description, false, null, installData);
        this.choices = choices;
        this.selected = selected;
    }

    /**
     * Returns the choices.
     *
     * @return the choices. The key is the choice identifier, the value, the display text
     */
    public List<T> getChoices()
    {
        return choices;
    }

    /**
     * Returns the selected choice.
     *
     * @return the selected choice, or {@code -1} if no choice is selected
     */
    public int getSelectedIndex()
    {
        return selected;
    }
}