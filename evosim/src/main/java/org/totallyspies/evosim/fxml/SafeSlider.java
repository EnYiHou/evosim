package org.totallyspies.evosim.fxml;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.NumberStringConverter;

import java.util.Optional;

/**
 * Slider that alerts user to confirm when a value is set outside of the range
 * of the slider.
 *
 * @author ptrstr
 */
public final class SafeSlider extends VBox {
    /**
     * Spacing to use between all components.
     */
    private static final double SPACING = 15;

    /**
     * Top-left padding to be used for all components.
     */
    private static final double PADDING = 5;

    /**
     * Default minimum value to use for slider.
     */
    private static final Number DEFAULT_MIN = 0;

    /**
     * Default maximum value to use for slider.
     */
    private static final Number DEFAULT_MAX = 100;

    /**
     * Default value to use for slider.
     */
    private static final Number DEFAULT_VALUE = 50;

    /**
     * Default min/max enforcement.
     */
    private static final boolean DEFAULT_HARDLIMIT = false;

    /**
     * Default name to use.
     */
    private static final String DEFAULT_NAME = "Safe slider";

    /**
     * Default setting for whether slider is floating point or integer.
     */
    private static final boolean DEFAULT_FLOATING_POINT = true;

    /**
     * Name of the variable changed.
     */
    private final SimpleStringProperty name;

    /**
     * Whether to use a floating point value or not for this slider.
     */
    private final SimpleBooleanProperty floatingPoint;

    /**
     * Whether the minimum should be enforced.
     */
    private final SimpleBooleanProperty hardMin;

    /**
     * Whether the maximum should be enforced.
     */
    private final SimpleBooleanProperty hardMax;


    /**
     * Slider JavaFX node that is shown in the UI.
     * Used to scroll the value between the specified range.
     */
    private final Slider slider;

    /**
     * TextField JavaFX node that is shown in the UI.
     * Used to manually set the value stored in the slider.
     */
    private final TextField textField;

    /**
     * Minimum safe value for the slider.
     * Floating point if {@link #floatingPoint} is true or integer if false.
     */
    private Property<Number> min;

    /**
     * Maximum safe value for the slider.
     * Floating point if {@link #floatingPoint} is true or integer if false.
     */
    private Property<Number> max;

    /**
     * Value kept in the slider.
     * Floating point if {@link #floatingPoint} is true or integer if false.
     */
    private Property<Number> value;

    /**
     * Initializes SafeSlider using default values.
     * Used when initializing class via FXML.
     */
    public SafeSlider() {
        this.slider = new Slider();
        this.textField = new TextField();

        this.hardMin = new SimpleBooleanProperty(DEFAULT_HARDLIMIT);
        this.hardMax = new SimpleBooleanProperty(DEFAULT_HARDLIMIT);

        this.floatingPoint = new SimpleBooleanProperty(DEFAULT_FLOATING_POINT);
        this.floatingPoint.addListener(
            (observable, oldValue, newValue) -> this.initializeValues()
        );

        this.name = new SimpleStringProperty(DEFAULT_NAME);

        this.initializeValues();
        this.setupLayout();
    }

    /**
     * Sets up the JavaFX layout for the children of this element.
     */
    private void setupLayout() {
        final Label label = new Label();
        label.setPadding(new Insets(PADDING, 0, 0, PADDING));
        label.textProperty().bind(this.name);

        this.setSpacing(SPACING);

        final HBox controlContainer = new HBox(this.slider, this.textField);
        controlContainer.setSpacing(SPACING);

        this.getChildren().addAll(label, controlContainer);
    }

    /**
     * Constructs a {@link SimpleDoubleProperty} based on {@code baseValue}'s
     * double value if {@link #floatingPoint} is set to true, or else constructs
     * a {@link SimpleIntegerProperty} based on {@code baseValue}'s integer
     * value.
     * @param baseValue Value for the property to be based on.
     * @return Property respecting type of {@link #floatingPoint} based on
     * {@code baseValue}.
     */
    private Property<Number> constructProperty(final Number baseValue) {
        return this.floatingPoint.get()
            ? new SimpleDoubleProperty(baseValue.doubleValue())
            : new SimpleIntegerProperty(baseValue.intValue());
    }

    private void initializeValues() {
        if (this.min != null) {
            this.slider.minProperty().unbindBidirectional(this.min);
        }

        if (this.max != null) {
            this.slider.maxProperty().unbindBidirectional(this.max);
        }

        if (this.value != null) {
            this.slider.valueProperty().unbindBidirectional(this.value);
            this.textField.textProperty().unbindBidirectional(this.value);
        }

        this.min = this.constructProperty(
            this.min == null ? DEFAULT_MIN : this.min.getValue()
        );

        this.max = this.constructProperty(
            this.max == null ? DEFAULT_MAX : this.max.getValue()
        );

        this.value = this.constructProperty(
            this.value == null ? DEFAULT_VALUE : this.value.getValue()
        );

        this.value.addListener(
            (observable, oldValue, newValue) ->
                onNewValue(this.value, oldValue, newValue)
        );

        this.slider.minProperty().bindBidirectional(this.min);
        this.slider.maxProperty().bindBidirectional(this.max);
        this.slider.valueProperty().bindBidirectional(this.value);
        this.textField.textProperty().bindBidirectional(
            this.value, new NumberStringConverter()
        );
    }

    /**
     * Event called on new value set for {@link #value}. Verifies if the value
     * is within the safe range and confirms with the user before using it.
     * @param valueProperty Property that has been set. Not using class instance
     *                      so that instances to be garbage collected do not
     *                      change the real value.
     * @param oldValue Old value of the property
     * @param newValue New value of the property to be checked
     */
    private void onNewValue(
        final Property<Number> valueProperty,
        final Number oldValue,
        final Number newValue
    ) {
        if (
            this.numbersEqual(oldValue, newValue)
            || (
                this.getMin().doubleValue() <= newValue.doubleValue()
                && newValue.doubleValue() <= this.getMax().doubleValue()
            )
        ) {
            return;
        }

        boolean isError = (
            (
                newValue.doubleValue() < this.getMin().doubleValue()
                && this.isHardMin()
            )
            || (
                this.getMax().doubleValue() < newValue.doubleValue()
                && this.isHardMax()
            )
        );

        Alert alert = new Alert(
            isError ? Alert.AlertType.ERROR : Alert.AlertType.CONFIRMATION
        );

        alert.setTitle(isError ? "Error" : "Warning");
        alert.setHeaderText("Evosim");

        final String rangeText = this.isFloatingPoint()
            ? String.format(
                "%.02f to %.02f",
                this.getMin().doubleValue(),
                this.getMax().doubleValue()
            )
            : String.format(
                "%d to %d",
                this.getMin().longValue(),
                this.getMax().longValue()
            );

        alert.setContentText(String.format(
            "Value set falls out of safe range (%s).", rangeText
        ));

        if (!isError) {
            alert.setContentText(alert.getContentText() + " Continue?");
        }

        Optional<ButtonType> result = alert.showAndWait();
        if (isError || result.get() == ButtonType.CANCEL) {
            valueProperty.setValue(oldValue);
        }
    }

    /**
     * Tests equality between two {@link Number} instances with their
     * {@link Number#doubleValue()} if {@link #floatingPoint}, or else their
     * {@link Number#longValue()}.
     * @param n1 First number to compare
     * @param n2 Second number to compare
     * @return Whether both numbers are equal
     */
    private boolean numbersEqual(final Number n1, final Number n2) {
        return this.isFloatingPoint()
            ? n1.doubleValue() == n2.doubleValue()
            : n1.longValue() == n2.longValue();
    }

    /**
     * Gets value within {@link #min}.
     * @return Value within
     */
    public Number getMin() {
        return min.getValue();
    }

    /**
     * Sets value within {@link #min}.
     * @param newMin New value to be used
     */
    public void setMin(final Number newMin) {
        this.min.setValue(newMin);
    }

    /**
     * Gets {@link #min}.
     * @return {@link #min}
     */
    public Property<Number> minProperty() {
        return min;
    }

    /**
     * Gets value within {@link #max}.
     * @return Value within
     */
    public Number getMax() {
        return max.getValue();
    }

    /**
     * Sets value within {@link #max}.
     * @param newMax New value to be used
     */
    public void setMax(final Number newMax) {
        this.max.setValue(newMax);
    }

    /**
     * Gets {@link #max}.
     * @return {@link #max}
     */
    public Property<Number> maxProperty() {
        return max;
    }

    /**
     * Gets value within {@link #value}.
     * @return Value within
     */
    public Number getValue() {
        return value.getValue();
    }

    /**
     * Sets value within {@link #value}.
     * @param newValue New value to be used
     */
    public void setValue(final Number newValue) {
        this.value.setValue(newValue);
    }

    /**
     * Gets {@link #value}.
     * @return {@link #value}
     */
    public Property<Number> valueProperty() {
        return value;
    }

    /**
     * Gets value within {@link #name}.
     * @return Value within
     */
    public String getName() {
        return name.get();
    }

    /**
     * Sets value within {@link #name}.
     * @param newName New value to be used
     */
    public void setName(final String newName) {
        this.name.set(newName);
    }

    /**
     * Gets {@link #name}.
     * @return {@link #name}
     */
    public SimpleStringProperty nameProperty() {
        return name;
    }

    /**
     * Gets value within {@link #floatingPoint}.
     * @return Value within
     */
    public boolean isFloatingPoint() {
        return floatingPoint.get();
    }

    /**
     * Sets value within {@link #floatingPoint}.
     * @param newFloatingPoint New value to be used
     */
    public void setFloatingPoint(final boolean newFloatingPoint) {
        this.floatingPoint.set(newFloatingPoint);
    }

    /**
     * Gets {@link #floatingPoint}.
     * @return {@link #floatingPoint}
     */
    public SimpleBooleanProperty floatingPointProperty() {
        return floatingPoint;
    }

    /**
     * Gets value within {@link #hardMin}.
     * @return Value within
     */
    public boolean isHardMin() {
        return hardMin.get();
    }

    /**
     * Gets {@link #hardMin}.
     * @return {@link #hardMin}
     */
    public SimpleBooleanProperty hardMinProperty() {
        return hardMin;
    }

    /**
     * Sets value within {@link #hardMin}.
     * @param newHardMin New value to be used
     */
    public void setHardMin(final boolean newHardMin) {
        this.hardMin.set(newHardMin);
    }

    /**
     * Gets value within {@link #hardMax}.
     * @return Value within
     */
    public boolean isHardMax() {
        return hardMax.get();
    }

    /**
     * Gets {@link #hardMax}.
     * @return {@link #hardMax}
     */
    public SimpleBooleanProperty hardMaxProperty() {
        return hardMax;
    }

    /**
     * Sets value within {@link #hardMax}.
     * @param newHardMax New value to be used
     */
    public void setHardMax(final boolean newHardMax) {
        this.hardMax.set(newHardMax);
    }
}
