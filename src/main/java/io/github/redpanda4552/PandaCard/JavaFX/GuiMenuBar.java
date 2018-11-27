/**
 * This file is part of PandaCard, licensed under the MIT License (MIT)
 * 
 * Copyright (c) 2018 Brian Wood
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.redpanda4552.PandaCard.JavaFX;

import io.github.redpanda4552.PandaCard.Main;
import io.github.redpanda4552.PandaCard.JavaFX.Menus.AbstractMenu;
import io.github.redpanda4552.PandaCard.JavaFX.Menus.MenuExport;
import io.github.redpanda4552.PandaCard.JavaFX.Menus.MenuFile;
import io.github.redpanda4552.PandaCard.JavaFX.Menus.MenuHelp;
import io.github.redpanda4552.PandaCard.JavaFX.Menus.MenuModify;
import io.github.redpanda4552.PandaCard.JavaFX.Menus.MenuView;
import javafx.scene.control.MenuBar;

public class GuiMenuBar extends MenuBar {

    private AbstractMenu menuFile, menuModify, menuExport, menuView, menuHelp;
    
    public GuiMenuBar(Main main, Gui guiMain) {
        menuFile = new MenuFile(main, guiMain, this, "File");
        menuModify = new MenuModify(main, guiMain, this, "Modify");
        menuExport = new MenuExport(main, guiMain, this, "Export");
        menuView = new MenuView(main, guiMain, this, "View");
        menuHelp = new MenuHelp(main, guiMain, this, "Help");
        getMenus().addAll(menuFile, menuModify, menuExport, menuView, menuHelp);
    }
    
    // Probably could do something with reflection here instead of manually firing these.
    // But that means I'd have to do something with reflection. :thonk: 
    public void update() {
        menuFile.update();
        menuModify.update();
        menuExport.update();
        menuView.update();
        menuHelp.update();
    }
}
