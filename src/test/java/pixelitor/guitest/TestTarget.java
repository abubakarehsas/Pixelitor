/*
 * Copyright 2018 Laszlo Balazs-Csiki and Contributors
 *
 * This file is part of Pixelitor. Pixelitor is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License, version 3 as published by the Free
 * Software Foundation.
 *
 * Pixelitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Pixelitor. If not, see <http://www.gnu.org/licenses/>.
 */

package pixelitor.guitest;

/**
 * Test targets: "all" (default),
 * "tools" (includes "Selection" menus),
 * "file", (the "File" menu with the exception of auto paint)
 * "autopaint",
 * "edit", ("Edit" and "Image" menus)
 * "filters" ("Colors" and "Filters" menus),
 * "layers" ("Layers" menus and layer buttons),
 * "develop",
 * "rest" ("View" and "Help" menus)
 */
public enum TestTarget {
    ALL {
        @Override
        public void run(AssertJSwingTest tester) {
            tester.testAll();
        }
    }, TOOLS {
        @Override
        public void run(AssertJSwingTest tester) {
            tester.testTools();
        }
    }, FILE {
        @Override
        public void run(AssertJSwingTest tester) {
            tester.testFileMenu();
        }
    }, AUTOPAINT {
        @Override
        public void run(AssertJSwingTest tester) {
            tester.testAutoPaint();
        }
    }, EDIT {
        @Override
        public void run(AssertJSwingTest tester) {
            tester.testEditMenu();
        }
    }, FILTERS {
        @Override
        public void run(AssertJSwingTest tester) {
            tester.testFilters();
        }
    }, LAYERS {
        @Override
        public void run(AssertJSwingTest tester) {
            tester.testLayers();
        }
    }, DEVELOP {
        @Override
        public void run(AssertJSwingTest tester) {
            tester.testDevelopMenu();
        }
    }, REST {
        @Override
        public void run(AssertJSwingTest tester) {
            tester.testViewMenu();
            tester.testColors();
            tester.testHelpMenu();
        }
    };

    public abstract void run(AssertJSwingTest tester);
}
