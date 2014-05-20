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
package teal.ui.swing;

import teal.ui.swing.plaf.JTaskPaneGroupAddon;
import teal.ui.swing.plaf.LookAndFeelAddons;
import teal.ui.swing.plaf.TaskPaneGroupUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * <code>JTaskPaneGroup</code> is a container for tasks and other
 * arbitrary components. <code>JTaskPaneGroup</code> s are added to
 * a {@link teal.ui.swing.JTaskPane}.
 * <code>JTaskPaneGroup</code> provides control to be expanded and
 * collapsed in order to show or hide the task list. It can have an
 * <code>icon</code>, a <code>text</code> and can be marked as
 * <code>special</code>. Marking a <code>JTaskPaneGroup</code> as
 * <code>special</code> is only an hint for the pluggable UI which
 * will usually paint it differently (by example by using another
 * color for the border of the pane).
 * 
 * When the JTaskPaneGroup is expanded or collapsed, it will be
 * animated with a fade effect. The animated can be disabled on a per
 * component basis through {@link #setAnimated(boolean)}.
 * 
 * To disable the animation for all newly created <code>JTaskPaneGroup</code>,
 * use the UIManager property:
 * <code>UIManager.put("TaskPaneGroup.animate", Boolean.FALSE);</code>.
 * 
 * @javabean.attribute
 *          name="isContainer"
 *          value="Boolean.TRUE"
 *          rtexpr="true"
 *          
 * @javabean.attribute
 *          name="containerDelegate"
 *          value="getContentPane"
 *          
 * @javabean.class
 *          name="JTaskPaneGroup"
 *          shortDescription="JTaskPaneGroup is a container for tasks and other arbitrary components."
 *          stopClass="java.awt.Component"
 * 
 * @javabean.icons
 *          mono16="JTaskPaneGroup16-mono.gif"
 *          color16="JTaskPaneGroup16.gif"
 *          mono32="JTaskPaneGroup32-mono.gif"
 *          color32="JTaskPaneGroup32.gif"
 */
public class JTaskPaneGroup extends JPanel implements
  JCollapsiblePane.JCollapsiblePaneContainer {

  public final static String UI_CLASS_ID = "TaskPaneGroupUI";
  
  // ensure at least the default ui is registered
  static {
    LookAndFeelAddons.contribute(new JTaskPaneGroupAddon());
  }

  /**
   * Used when generating PropertyChangeEvents for the "expanded" property
   */
  public static final String EXPANDED_CHANGED_KEY = "expanded";

  /**
   * Used when generating PropertyChangeEvents for the "scrollOnExpand" property
   */
  public static final String SCROLL_ON_EXPAND_CHANGED_KEY = "scrollOnExpand";

  /**
   * Used when generating PropertyChangeEvents for the "text" property
   */
  public static final String TEXT_CHANGED_KEY = "text";

  /**
   * Used when generating PropertyChangeEvents for the "icon" property
   */
  public static final String ICON_CHANGED_KEY = "icon";

  /**
   * Used when generating PropertyChangeEvents for the "special" property
   */
  public static final String SPECIAL_CHANGED_KEY = "special";

  /**
   * Used when generating PropertyChangeEvents for the "animated" property
   */
  public static final String ANIMATED_CHANGED_KEY = "animated";

  private String text;
  private Icon icon;
  private boolean special;
  private boolean expanded = true;
  private boolean scrollOnExpand;

  private JCollapsiblePane collapsePane;
  
  /**
   * Creates a new empty <code>JTaskPaneGroup</code>.
   */
  public JTaskPaneGroup() {
    collapsePane = new JCollapsiblePane();
    super.setLayout(new BorderLayout(0, 0));
    super.addImpl(collapsePane, BorderLayout.CENTER, -1);
    
    updateUI();
    setFocusable(true);
    setOpaque(false);

    // disable animation if specified in UIManager
    setAnimated(!Boolean.FALSE.equals(UIManager.get("TaskPaneGroup.animate")));
  }

  public Container getContentPane() {
    return collapsePane.getContentPane();
  }
  
  /**
   * Notification from the <code>UIManager</code> that the L&F has changed.
   * Replaces the current UI object with the latest version from the <code>UIManager</code>.
   * 
   * @see javax.swing.JComponent#updateUI
   */
  public void updateUI() {
    // collapsePane is null when updateUI() is called by the "super()"
    // constructor
    if (collapsePane == null) {
      return;
    }
    setUI((TaskPaneGroupUI)LookAndFeelAddons.getUI(this, TaskPaneGroupUI.class,
      UIManager.getUI(this)));
  }
  
  /**
   * Sets the L&F object that renders this component.
   * 
   * @param ui the <code>TaskPaneGroupUI</code> L&F object
   * @see javax.swing.UIDefaults#getUI
   * 
   * @beaninfo bound: true hidden: true description: The UI object that
   * implements the taskpane group's LookAndFeel.
   */
  public void setUI(TaskPaneGroupUI ui) {
    super.setUI(ui);
  }

  /**
   * Returns the name of the L&F class that renders this component.
   * 
   * @return the string {@link #UI_CLASS_ID}
   * @see javax.swing.JComponent#getUIClassID
   * @see javax.swing.UIDefaults#getUI
   */
  public String getUIClassID() {
    return UI_CLASS_ID;
  }

  /**
   * Returns the text currently displayed in the border of this pane.
   * 
   * @return the text currently displayed in the border of this pane
   */
  public String getText() {
    return text;
  }

  /**
   * Sets the text to be displayed in the border of this pane.
   * 
   * @param text the text to be displayed in the border of this pane
   * @javabean.attribute
   *          bound="true"
   *          preferred="true"
   */
  public void setText(String text) {
    String old = text;
    this.text = text;
    firePropertyChange(TEXT_CHANGED_KEY, old, text);
  }

  /**
   * Returns the icon currently displayed in the border of this pane.
   * 
   * @return the icon currently displayed in the border of this pane
   */
  public Icon getIcon() {
    return icon;
  }

  /**
   * Sets the icon to be displayed in the border of this pane. Some pluggable
   * UIs may impose size constraints for the icon. A size of 16x16 pixels is
   * the recommended icon size.
   * 
   * @param icon the icon to be displayed in the border of this pane
   * @javabean.attribute
   *          bound="true"
   *          preferred="true"
   */
  public void setIcon(Icon icon) {
    Icon old = icon;
    this.icon = icon;
    firePropertyChange(ICON_CHANGED_KEY, old, icon);
  }

  /**
   * Returns true if this pane is "special".
   * 
   * @return true if this pane is "special"
   */
  public boolean isSpecial() {
    return special;
  }

  /**
   * Sets this pane to be "special" or not.
   * 
   * @param special true if this pane is "special", false otherwise
   * @javabean.attribute
   *          bound="true"
   *          preferred="true"
   */
  public void setSpecial(boolean special) {
    if (this.special != special) {
      this.special = special;
      firePropertyChange(SPECIAL_CHANGED_KEY, !special, special);
    }
  }

  /**
   * Should this group be scrolled to be visible on expand.
   * 
   * 
   * @param scrollOnExpand true to scroll this group to be
   * visible if this group is expanded.
   * 
   * @see #setExpanded(boolean)
   * 
   * @javabean.attribute
   *          bound="true"
   *          preferred="true"
   */
  public void setScrollOnExpand(boolean scrollOnExpand) {
    if (this.scrollOnExpand != scrollOnExpand) {
      this.scrollOnExpand = scrollOnExpand;
      firePropertyChange(SCROLL_ON_EXPAND_CHANGED_KEY,
        !scrollOnExpand, scrollOnExpand);
    }
  }
  
  /**
   * Should this group scroll to be visible after
   * this group was expanded.
   * 
   * @return true if we should scroll false if nothing
   * should be done.
   */
  public boolean isScrollOnExpand() {
    return scrollOnExpand;
  }
  
  /**
   * Expands or collapses this group.
   * 
   * @param expanded true to expand the group, false to collapse it
   * @javabean.attribute
   *          bound="true"
   *          preferred="true"
   */
  public void setExpanded(boolean expanded) {
    if (this.expanded != expanded) {
      this.expanded = expanded;
      collapsePane.setCollapsed(!expanded);
      firePropertyChange(EXPANDED_CHANGED_KEY, !expanded, expanded);
    }
  }

  /**
   * Returns true if this taskpane is expanded, false if it is collapsed.
   * 
   * @return true if this taskpane is expanded, false if it is collapsed.
   */
  public boolean isExpanded() {
    return expanded;
  }

  /**
   * Enables or disables animation during expand/collapse transition.
   * 
   * @param animated
   * @javabean.attribute
   *          bound="true"
   *          preferred="true"
   */
  public void setAnimated(boolean animated) {
    if (isAnimated() != animated) {
      collapsePane.setAnimated(animated);
      firePropertyChange(ANIMATED_CHANGED_KEY, !isAnimated(), isAnimated());
    }
  }
  
  /**
   * @return true if this taskpane is animated during expand/collapse
   *         transition.
   */
  public boolean isAnimated() {
    return collapsePane.isAnimated();
  }
  
  /**
   * Adds an action to this <code>JTaskPaneGroup</code>. Returns a
   * component built from the action. The returned component has been
   * added to the <code>JTaskPaneGroup</code>.
   * 
   * @param action
   * @return a component built from the action
   */
  public Component add(Action action) {
    Component c = ((TaskPaneGroupUI)ui).createAction(action);
    add(c);
    return c;
  }

  public Container getValidatingContainer() {
    return getParent();
  }
  
  protected void addImpl(Component comp, Object constraints, int index) {
    getContentPane().add(comp, constraints, index);
  }

  public void setLayout(LayoutManager mgr) {
    if (collapsePane != null) {
      getContentPane().setLayout(mgr);
    }
  }
  
  /**
   * Overriden to redirect call to the content pane
   */
  public void remove(Component comp) {
    getContentPane().remove(comp);
  }

  /**
   * Overriden to redirect call to the content pane.
   */
  public void remove(int index) {
    getContentPane().remove(index);
  }
  
  /**
   * Overriden to redirect call to the content pane.
   */
  public void removeAll() {
    getContentPane().removeAll();
  }
  
  /**
   * @see JComponent#paramString()
   */
  protected String paramString() {
    return super.paramString()
      + ",text="
      + getText()
      + ",icon="
      + getIcon()
      + ",expanded="
      + String.valueOf(isExpanded())
      + ",special="
      + String.valueOf(isSpecial())
      + ",ui=" + getUI();
  }

}
