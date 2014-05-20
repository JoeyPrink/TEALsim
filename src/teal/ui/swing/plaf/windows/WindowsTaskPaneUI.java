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
package teal.ui.swing.plaf.windows;

import teal.ui.swing.plaf.basic.BasicTaskPaneUI;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/**
 * Windows implementation of the TaskPaneUI.
 */
public class WindowsTaskPaneUI extends BasicTaskPaneUI {

  public static ComponentUI createUI(JComponent c) {
    return new WindowsTaskPaneUI();
  }

  public void installUI(JComponent c) {
    super.installUI(c);
  }
    
}
