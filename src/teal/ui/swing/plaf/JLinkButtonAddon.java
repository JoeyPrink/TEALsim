/**
 * $ $ License.
 *
 * Copyright $ L2FProd.com
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
package teal.ui.swing.plaf;

import teal.ui.swing.JLinkButton;
import teal.ui.swing.plaf.windows.WindowsLookAndFeelAddons;

/**
 * Add on for <code>JLinkButton</code>.
 */
public class JLinkButtonAddon implements ComponentAddon {

  public String getName() {
    return "JLinkButton";
  }

  public void initialize(LookAndFeelAddons addon) {
    addon.loadDefaults(new Object[] {JLinkButton.UI_CLASS_ID,
      "teal.ui.swing.plaf.basic.BasicLinkButtonUI",});

    if (addon instanceof WindowsLookAndFeelAddons) {
      addon.loadDefaults(new Object[] {JLinkButton.UI_CLASS_ID,
        "teal.ui.swing.plaf.windows.WindowsLinkButtonUI"});
    }
  }

  public void uninitialize(LookAndFeelAddons addon) {
  }

}
