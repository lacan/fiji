package fiji.plugin.trackmate.gui;

import static fiji.plugin.trackmate.gui.TrackMateFrame.FONT;
import static fiji.plugin.trackmate.gui.TrackMateFrame.SMALL_FONT;
import static fiji.plugin.trackmate.visualization.trackscheme.TrackSchemeFrame.TRACK_SCHEME_ICON;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.EnumMap;
import java.util.HashSet;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;

import fiji.plugin.trackmate.Feature;
import fiji.plugin.trackmate.TrackMateModel;
import fiji.plugin.trackmate.util.TMUtils;
import fiji.plugin.trackmate.visualization.TrackMateModelView;
import fiji.plugin.trackmate.visualization.TrackMateModelView.TrackDisplayMode;

/**
 * A configuration panel used to tune the aspect of spots and tracks in multiple {@link TrackMateModelView}.
 * This GUI takes the role of a controller.
 * @author Jean-Yves Tinevez <tinevez@pasteur.fr>   -  2010 - 2011
 */
public class DisplayerPanel extends ActionListenablePanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	{
		//Set Look & Feel
		try {
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public ActionEvent TRACK_DISPLAY_MODE_CHANGED 	= new ActionEvent(this, 0, "TrackDisplayModeChanged");
	public ActionEvent TRACK_VISIBILITY_CHANGED 	= new ActionEvent(this, 1, "TrackVisibilityChanged");
	public ActionEvent SPOT_COLOR_MODE_CHANGED; // instantiate later, from color gui panel
	public ActionEvent SPOT_VISIBILITY_CHANGED 		= new ActionEvent(this, 2, "SpotVisibilityChanged");
	public ActionEvent TRACK_SCHEME_BUTTON_PRESSED 	= new ActionEvent(this, 3, "TrackSchemeButtonPushed");
	public ActionEvent SPOT_DISPLAY_RADIUS_CHANGED 	= new ActionEvent(this, 4, "SpotDisplayRadiusChanged");
	public ActionEvent SPOT_DISPLAY_LABEL_CHANGED 	= new ActionEvent(this, 5, "SpotDisplayLabelChanged");

	private JLabel jLabelTrackDisplayMode;
	private JComboBox jComboBoxDisplayMode;
	private JLabel jLabelDisplayOptions;
	private JPanel jPanelSpotOptions;
	private JCheckBox jCheckBoxDisplaySpots;
	private JPanel jPanelTrackOptions;
	private JCheckBox jCheckBoxDisplayTracks;
	private JCheckBox jCheckBoxLimitDepth;
	private JTextField jTextFieldFrameDepth;
	private JLabel jLabelFrameDepth;
	private JPanelSpotColorGUI jPanelSpotColor;
	private EnumMap<Feature, double[]> featureValues;
	private JButton jButtonShowTrackScheme;
	private JNumericTextField jTextFieldSpotRadius;
	private JCheckBox jCheckBoxDisplayNames;

	/**
	 * The set of {@link TrackMateModelView} views controlled by this controller.
	 * It is a set, so that we work around trying to link multiple times with the 
	 * same displayer.
	 */
	private HashSet<TrackMateModelView> spotDisplayers = new HashSet<TrackMateModelView>();
		
	public DisplayerPanel(TrackMateModel model) {
		super();
		setModel(model);
		initGUI();
		addActionListener(this);
	}
	

	/*
	 * PUBLIC METHODS
	 */
	
	/**
	 * Add the given {@link TrackMateModelView} to the list managed by this controller.
	 */
	public void register(final TrackMateModelView spotDisplayer) {
		this.spotDisplayers.add(spotDisplayer);
	}
	
	public double getSpotDisplayRadiusRatio() {
		return jTextFieldSpotRadius.getValue();
	}
	
	public TrackDisplayMode getTrackDisplayMode() {
		return TrackDisplayMode.values()[jComboBoxDisplayMode.getSelectedIndex()];
	}
	
	public int getTrackDisplayDepth() {
		if (jCheckBoxLimitDepth.isSelected())
			return Integer.parseInt(jTextFieldFrameDepth.getText());
		else
			return Integer.MAX_VALUE;
	}
	
	public boolean isDisplayTrackSelected() {
		return jCheckBoxDisplayTracks.isSelected();
	}
	
	public boolean isDisplaySpotSelected() {
		return jCheckBoxDisplaySpots.isSelected();
	}
	
	public Feature getColorSpotByFeature() {
		return jPanelSpotColor.setColorByFeature;
	}

	public boolean isDisplaySpotNameSelected() {
		return jCheckBoxDisplayNames.isSelected();
	}
	
	/*
	 * PRIVATE METHODS
	 */
	
	private void setModel(TrackMateModel model) {
		this.featureValues = TMUtils.getFeatureValues(model.getSelectedSpots().values());
	}
	
	private void displayTrackFlagChanged(boolean display) {
		jPanelTrackOptions.setEnabled(display);
		fireAction(TRACK_VISIBILITY_CHANGED);
	}
	
	private void spotColorModeChanged() {
		fireAction(SPOT_COLOR_MODE_CHANGED);
	}
	private void displaySpotFlagChanged(boolean display) {
		jPanelSpotOptions.setEnabled(display);
		fireAction(SPOT_VISIBILITY_CHANGED);
	}
	
	
	private void trackDisplayModeChanged(ActionEvent e) {
		fireAction(TRACK_DISPLAY_MODE_CHANGED);
	}
	
	private void initGUI() {
		final ActionListener trackDisplayModeListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				trackDisplayModeChanged(e);
			}
		};
		
		
		try {
			this.setPreferredSize(new java.awt.Dimension(268, 469));
			this.setSize(300, 500);
			this.setLayout(null);
			{
				jPanelTrackOptions = new JPanel() {
					private static final long serialVersionUID = -1805693239189343720L;
					public void setEnabled(boolean enabled) {
						for(Component c : getComponents())
							c.setEnabled(enabled);
					};
				};
				FlowLayout jPanelTrackOptionsLayout = new FlowLayout();
				jPanelTrackOptionsLayout.setAlignment(FlowLayout.LEFT);
				jPanelTrackOptions.setLayout(jPanelTrackOptionsLayout);
				this.add(jPanelTrackOptions);
				jPanelTrackOptions.setBounds(10, 212, 280, 117);
				jPanelTrackOptions.setBorder(new LineBorder(new java.awt.Color(192,192,192), 1, true));
				{
					jLabelTrackDisplayMode = new JLabel();
					jPanelTrackOptions.add(jLabelTrackDisplayMode);
					jLabelTrackDisplayMode.setText("  Track display mode:");
					jLabelTrackDisplayMode.setBounds(10, 163, 268, 15);
					jLabelTrackDisplayMode.setFont(FONT);
					jLabelTrackDisplayMode.setPreferredSize(new java.awt.Dimension(261, 14));
				}
				{
					TrackDisplayMode[] modes = TrackMateModelView.TrackDisplayMode.values();
					String[] keyNames = new String[modes.length];
					for (int i = 0; i < keyNames.length; i++)
						keyNames[i] = modes[i].toString();
					ComboBoxModel jComboBoxDisplayModeModel = new DefaultComboBoxModel(keyNames);
					jComboBoxDisplayMode = new JComboBox();
					jPanelTrackOptions.add(jComboBoxDisplayMode);
					jComboBoxDisplayMode.setModel(jComboBoxDisplayModeModel);
					jComboBoxDisplayMode.setSelectedIndex(TrackMateModelView.DEFAULT_TRACK_DISPLAY_MODE.ordinal());
					jComboBoxDisplayMode.setFont(SMALL_FONT);
					jComboBoxDisplayMode.setPreferredSize(new java.awt.Dimension(265, 27));
					jComboBoxDisplayMode.addActionListener(trackDisplayModeListener);
				}
				{
					jCheckBoxLimitDepth = new JCheckBox();
					jPanelTrackOptions.add(jCheckBoxLimitDepth);
					jCheckBoxLimitDepth.setText("Limit frame depth");
					jCheckBoxLimitDepth.setBounds(6, 216, 272, 23);
					jCheckBoxLimitDepth.setFont(FONT);
					jCheckBoxLimitDepth.setSelected(true);
					jCheckBoxLimitDepth.setPreferredSize(new java.awt.Dimension(259, 23));
					jCheckBoxLimitDepth.addActionListener(trackDisplayModeListener);
				}
				{
					jLabelFrameDepth = new JLabel();
					jPanelTrackOptions.add(jLabelFrameDepth);
					jLabelFrameDepth.setText("  Frame depth:");
					jLabelFrameDepth.setFont(SMALL_FONT);
					jLabelFrameDepth.setPreferredSize(new java.awt.Dimension(103, 14));
				}
				{
					jTextFieldFrameDepth = new JTextField();
					jPanelTrackOptions.add(jTextFieldFrameDepth);
					jTextFieldFrameDepth.setText("10");
					jTextFieldFrameDepth.setFont(SMALL_FONT);
					jTextFieldFrameDepth.setText(""+TrackMateModelView.DEFAULT_TRACK_DISPLAY_DEPTH);
					jTextFieldFrameDepth.setPreferredSize(new java.awt.Dimension(34, 20));
					jTextFieldFrameDepth.addActionListener(trackDisplayModeListener);
				}
			}
			{
				jCheckBoxDisplayTracks = new JCheckBox();
				this.add(jCheckBoxDisplayTracks);
				jCheckBoxDisplayTracks.setText("Display tracks");
				jCheckBoxDisplayTracks.setFont(FONT);
				jCheckBoxDisplayTracks.setBounds(10, 188, 233, 23);
				jCheckBoxDisplayTracks.setSelected(true);
				jCheckBoxDisplayTracks.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						displayTrackFlagChanged(jCheckBoxDisplayTracks.isSelected());
					}
				});
			}
			{
				jCheckBoxDisplaySpots = new JCheckBox();
				this.add(jCheckBoxDisplaySpots);
				jCheckBoxDisplaySpots.setText("Display spots");
				jCheckBoxDisplaySpots.setFont(FONT);
				jCheckBoxDisplaySpots.setBounds(10, 38, 280, 23);
				jCheckBoxDisplaySpots.setSelected(true);
				jCheckBoxDisplaySpots.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						displaySpotFlagChanged(jCheckBoxDisplaySpots.isSelected());
					}
				});
			}
			{
				jPanelSpotOptions = new JPanel() {
					private static final long serialVersionUID = 3259314983744108471L;
					public void setEnabled(boolean enabled) {
						for(Component c : getComponents())
							c.setEnabled(enabled);
					};
				};
				FlowLayout jPanelSpotOptionsLayout = new FlowLayout();
				jPanelSpotOptionsLayout.setAlignment(FlowLayout.LEFT);
				jPanelSpotOptions.setLayout(jPanelSpotOptionsLayout);
				this.add(jPanelSpotOptions);
				jPanelSpotOptions.setBounds(10, 63, 280, 110);
				jPanelSpotOptions.setBorder(new LineBorder(new java.awt.Color(192,192,192), 1, true));
				{
					jPanelSpotColor = new JPanelSpotColorGUI(this);
					jPanelSpotColor.featureValues = featureValues;
					jPanelSpotOptions.add(jPanelSpotColor);
					SPOT_COLOR_MODE_CHANGED  = jPanelSpotColor.COLOR_FEATURE_CHANGED;
					jPanelSpotColor.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							spotColorModeChanged();
						}
					});
				}
				{
					JLabel jLabelSpotRadius = new JLabel();
					jLabelSpotRadius.setText("  Spot display radius ratio:");
					jLabelSpotRadius.setFont(SMALL_FONT);
					jPanelSpotOptions.add(jLabelSpotRadius);
					
					jTextFieldSpotRadius = new JNumericTextField("1");
					jTextFieldSpotRadius.setPreferredSize(new java.awt.Dimension(34, 20));
					jTextFieldSpotRadius.setFont(SMALL_FONT);
					jPanelSpotOptions.add(jTextFieldSpotRadius);
					jTextFieldSpotRadius.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							fireAction(SPOT_DISPLAY_RADIUS_CHANGED);
						}
					});
					jTextFieldSpotRadius.addFocusListener(new FocusListener() {
						@Override
						public void focusLost(FocusEvent e) {
							fireAction(SPOT_DISPLAY_RADIUS_CHANGED);							
						}
						@Override
						public void focusGained(FocusEvent e) {	}
					});
				}
				{
					jCheckBoxDisplayNames = new JCheckBox();
					jCheckBoxDisplayNames.setText("Display spot names");
					jCheckBoxDisplayNames.setFont(SMALL_FONT);
					jCheckBoxDisplayNames.setSelected(false);
					jPanelSpotOptions.add(jCheckBoxDisplayNames);
					jCheckBoxDisplayNames.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							fireAction(SPOT_DISPLAY_LABEL_CHANGED);
						}
					});
				}
			}
			{
				jLabelDisplayOptions = new JLabel();
				jLabelDisplayOptions.setText("Display options");
				jLabelDisplayOptions.setFont(FONT.deriveFont(Font.BOLD));
				jLabelDisplayOptions.setBounds(10, 11, 280, 14);
				jLabelDisplayOptions.setHorizontalAlignment(SwingConstants.CENTER);
				this.add(jLabelDisplayOptions);
			}
			{
				jButtonShowTrackScheme = new JButton();
				jButtonShowTrackScheme.setText("Track scheme");
				jButtonShowTrackScheme.setIcon(TRACK_SCHEME_ICON);
				jButtonShowTrackScheme.setFont(FONT);
				jButtonShowTrackScheme.setBounds(10, 345, 120, 30);
				jButtonShowTrackScheme.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						fireAction(TRACK_SCHEME_BUTTON_PRESSED);
					}
				});
				this.add(jButtonShowTrackScheme);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Fired when the user change a setting displayed on this GUI. 
	 */
	@Override
	public void actionPerformed(final ActionEvent event) {
		if (event == SPOT_COLOR_MODE_CHANGED) {
			for(TrackMateModelView spotDisplayer : spotDisplayers)
				spotDisplayer.setColorByFeature(getColorSpotByFeature());

		} else if (event == SPOT_VISIBILITY_CHANGED) {
			for(TrackMateModelView spotDisplayer : spotDisplayers)
				spotDisplayer.setSpotVisible(isDisplaySpotSelected());

		} else if (event == TRACK_DISPLAY_MODE_CHANGED) {
			for(TrackMateModelView spotDisplayer : spotDisplayers)
				spotDisplayer.setDisplayTrackMode(getTrackDisplayMode(), getTrackDisplayDepth());

		} else if (event == TRACK_VISIBILITY_CHANGED) {
			for(TrackMateModelView spotDisplayer : spotDisplayers)
				spotDisplayer.setTrackVisible(isDisplayTrackSelected());

		} else if (event == SPOT_DISPLAY_RADIUS_CHANGED) {
			for(TrackMateModelView spotDisplayer : spotDisplayers)
				spotDisplayer.setRadiusDisplayRatio((float) getSpotDisplayRadiusRatio());

		} else if (event == SPOT_DISPLAY_LABEL_CHANGED) {
			for(TrackMateModelView spotDisplayer : spotDisplayers)
				spotDisplayer.setSpotNameVisible(isDisplaySpotNameSelected());
		} // Other events (TrackScheme) will be dealt with by another class 		
	}
	/*
	 * MAIN METHOD
	 */
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		DisplayerPanel displayerPanel_IL = new DisplayerPanel(null);
		frame.getContentPane().add(displayerPanel_IL);
		displayerPanel_IL.setPreferredSize(new java.awt.Dimension(300, 469));
	}

	


}
