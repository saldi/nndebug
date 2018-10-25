package pl.jclab.nndebug.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;

import static pl.jclab.nndebug.config.AppCfg.pauseMode;
import static pl.jclab.nndebug.config.AppCfg.speed;
import static pl.jclab.nndebug.config.AppCfg.stepMode;

public class ControllPanel extends GridPane{

	protected Slider speedSlider;
	protected Button run;
	protected Button step;

	public ControllPanel(){

		initaializeStyle();
		initialize();
	}

	protected void initaializeStyle(){

		setHgap(5);
		setVgap(10);
		setAlignment(Pos.CENTER_LEFT);
	}

	protected void initialize(){

		speedSlider = new Slider();
		speedSlider.setMin(0);
		speedSlider.setMax(100);
		speedSlider.setValue(speed);
		speedSlider.setShowTickLabels(true);
		speedSlider.setShowTickMarks(true);
		speedSlider.setMajorTickUnit(50);
		speedSlider.setMinorTickCount(5);
		speedSlider.setBlockIncrement(10);

		speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {

			speed = newValue.intValue();

		});

		step = new Button("Step");
		run = new Button("Run");

		final String runStyle = run.getStyle();
		run.setOnAction(event -> {
			pauseMode = !pauseMode;
			if (pauseMode){
				run.setStyle(runStyle);
			} else {
				run.setStyle("-fx-background-color: #ff0000; ");
			}
		});

		step.setOnAction(event -> {
			stepMode = true;
		});

		add(speedSlider, 0, 0);
		add(run, 1, 0);
		add(step, 2, 0);
	}
}
