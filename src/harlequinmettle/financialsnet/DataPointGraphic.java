package harlequinmettle.financialsnet;

import harlequinmettle.financialsnet.interfaces.DBLabels;

import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;

public class DataPointGraphic {

	public static final int PIXELS_BORDER = 3;
	public static final int PIXELS_HEIGHT = 150;
	public static Color COLOR_TIME_LINE = Color.orange; //new Color(    );
	public static Color COLOR_HISTOGRAM_BAR  = Color.gray; //new Color(   1,1,1   );
	public static Color COLOR_LABEL_TEXT = Color.white;// new Color(   1,1,1   );
	//previously histos
	private Rectangle2D.Float[]  bars  ;
	private Rectangle2D.Float border;
	private Point2D.Float minMaxLine;
	private Point2D.Float minMaxBars;
	private GeneralPath timePath= new GeneralPath();
	private String category = "";
	private String ticker = "";
	private int heightFactor = 1;
	private int rank = 0;
	private int categoryId = 0;
	private float barwidth = (ProfileCanvas.W - 2*PIXELS_BORDER) / StatInfo.nbars;

	public DataPointGraphic(String category, String ticker){
		init(category,ticker);
	}

	public DataPointGraphic(String category, String ticker, int heightFactor){
		this.heightFactor = heightFactor;
		init(category,ticker);
		
	}

	private void init(String category2, String ticker2) {
		this.category = category;
		this.ticker = ticker;
		List list = Arrays.asList(DBLabels.priorityLabeling);
		this.rank = list.indexOf(category2);
		if(rank<0)return;
		  list = Arrays.asList(DBLabels.labels);
		this.categoryId = list.indexOf(category2);
		if(categoryId<0)return;
		bars = setUpBars(Database.statistics.get(categoryId).histogram);
		
	}

	public Rectangle2D.Float[] setUpBars(int[] histogram ) {
		int max = maxBar(histogram);
		Rectangle2D.Float[] histoBars = new Rectangle2D.Float[StatInfo.nbars];

		float graphicsScale = ((float) PIXELS_HEIGHT * heightFactor) / max;

		for (int i = 0; i < StatInfo.nbars; i++) {
			int top = PIXELS_BORDER + PIXELS_BORDER * rank + PIXELS_HEIGHT * rank
					+ (PIXELS_HEIGHT - (int) (graphicsScale * histogram[i]));
			int left = PIXELS_HEIGHT + (int) (barwidth) * i;
			int width = (int) (barwidth);
			int height = (int) (graphicsScale * histogram[i]);
			histoBars[i] = new Rectangle2D.Float(left, top, width, height);

		}
		return histoBars;
	}

	private int maxBar(int[] histogram) {
		int max = 0; 
		for (int i : histogram) {
			if (i > max) { 
				max = i;
			}
		}
		return max;
	}

}
