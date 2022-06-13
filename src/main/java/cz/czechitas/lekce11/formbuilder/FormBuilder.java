package cz.czechitas.lekce11.formbuilder;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.common.swing.MnemonicUtils;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Filip Jirsák
 */
public class FormBuilder<B> implements WithModel<B>, FormBuilderWithContainer<B>, WithLabel<B>, WithInput<B> {
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

  private final PresentationModel<B> model;
  private Container container;

  private JLabel label;
  private JComponent input;

  private FormBuilder(PresentationModel<B> model) {
    this.model = model;
  }

  public static <B> WithModel<B> create(PresentationModel<B> model) {
    return new FormBuilder<>(model);
  }

  @Override
  public FormBuilderWithContainer<B> container(Container container) {
    this.container = container;
    return this;
  }

  @Override
  public WithLabel<B> label(String text) {
    this.label = new JLabel();
    MnemonicUtils.configure(label, text);
    return this;
  }

  @Override
  public WithInput<B> textField(String property) {
    return textField(property, null);
  }

  @Override
  public WithInput<B> textField(String property, Consumer<JTextField> configuration) {
    JTextField textField = BasicComponentFactory.createTextField(model.getModel(property));

    if (configuration != null) {
      configuration.accept(textField);
    }

    Objects.requireNonNull(label);
    label.setLabelFor(textField);

    this.input = textField;
    return this;
  }

  private WithInput<B> formattedTextField(String property, JFormattedTextField.AbstractFormatter formatter, Consumer<JFormattedTextField> configuration) {
    JFormattedTextField formattedTextField = BasicComponentFactory.createFormattedTextField(model.getModel(property), formatter);

    if (configuration != null) {
      configuration.accept(formattedTextField);
    }

    Objects.requireNonNull(label);
    label.setLabelFor(formattedTextField);

    this.input = formattedTextField;
    return this;
  }

  @Override
  public WithInput<B> numberField(String property) {
    return numberField(property, (Consumer<JFormattedTextField>) null);
  }

  @Override
  public WithInput<B> numberField(String property, NumberFormat format) {
    return numberField(property, format, null);
  }

  @Override
  public WithInput<B> numberField(String property, String format) {
    return numberField(property, format, null);
  }

  @Override
  public WithInput<B> numberField(String property, Consumer<JFormattedTextField> configuration) {
    return formattedTextField(property, new NumberFormatter(), configuration);
  }

  @Override
  public WithInput<B> numberField(String property, NumberFormat format, Consumer<JFormattedTextField> configuration) {
    return formattedTextField(property, new NumberFormatter(format), configuration);
  }

  @Override
  public WithInput<B> numberField(String property, String format, Consumer<JFormattedTextField> configuration) {
    return numberField(property, new DecimalFormat(format), configuration);
  }

  @Override
  public WithInput<B> dateField(String property) {
    return dateField(property, null);
  }

  @Override
  public WithInput<B> dateField(String property, Consumer<JFormattedTextField> configuration) {
    return formattedTextField(property, new DateFormatter(dateFormat), configuration);
  }

  @Override
  public <E> WithInput<B> comboBox(String property, List<E> items) {
    return comboBox(property, items, null);
  }

  @Override
  public <E> WithInput<B> comboBox(String property, SelectionInList<E> selectionInList) {
    return comboBox(property, selectionInList, null);
  }

  @Override
  public <E> WithInput<B> comboBox(String property, List<E> items, Consumer<JComboBox<E>> configuration) {
    SelectionInList<E> selectionInList = new SelectionInList<>(items, model.getModel(property));
    return comboBox(property, selectionInList, configuration);
  }

  @Override
  public <E> WithInput<B> comboBox(String property, SelectionInList<E> selectionInList, Consumer<JComboBox<E>> configuration) {
    JComboBox<E> comboBox = BasicComponentFactory.createComboBox(selectionInList);

    if (configuration != null) {
      configuration.accept(comboBox);
    }

    Objects.requireNonNull(label);
    label.setLabelFor(comboBox);

    this.input = comboBox;
    return this;
  }

  @Override
  public WithInput<B> checkbox(String label, String property) {
    return checkbox(label, property, null);
  }

  @Override
  public WithInput<B> checkbox(String label, String property, Consumer<JCheckBox> configuration) {
    JCheckBox checkBox = BasicComponentFactory.createCheckBox(model.getModel(property), label);

    if (configuration != null) {
      configuration.accept(checkBox);
    }

    this.input = checkBox;
    return this;
  }

  @Override
  public WithInput<B> panel(Consumer<JPanel> configuration) {
    JPanel panel = new JPanel();
    configuration.accept(panel);

    this.input = panel;
    return this;
  }

  @Override
  public FormBuilderWithContainer<B> add() {
    Objects.requireNonNull(input);

    if (label != null) {
      container.add(label);
    }
    container.add(input);
    label = null;
    input = null;
    return this;
  }

  @Override
  public FormBuilderWithContainer<B> add(Object constraints) {
    Objects.requireNonNull(input);

    if (label != null) {
      container.add(label);
    }
    container.add(input, constraints);
    label = null;
    input = null;
    return this;
  }
}
