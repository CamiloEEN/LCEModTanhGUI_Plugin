import ij.plugin.*;
import ij.*;
import ij.gui.*;
import ij.process.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import ij.gui.Plot;
 

/**
    
*/
public class ModTanhCEEN_RGB_GUI implements PlugIn {

    //Constants definitions
    static final int L = 256;
    static final String configs[]= {"Default","Dark images","Bright Images"};
    
    //Transfer functions definitions
    double[] redChannelTransf= new double[L];
    double[] greenChannelTransf= new double[L];
    double[] blueChannelTransf= new double[L];
    double[] qValues = new double[L];
    
    //Generallized ModTanhCeen function definitions
    public double GCcoef(double lam, int q, int p1, int p2)
    {
        double Cc=(Math.exp(-lam*(p2-p1))-Math.exp(-lam*(p2+p1-2*q  )))/(p1-q);
        Cc=Cc+(Math.exp(-lam*(p2+p1-2*q))-Math.exp(lam*(p2-p1)))/(p2-q);
                
        return Cc;
    }
    
        
    public double GModTanhCeen(int q, double lam, int q0, int p1, int p2)
    {
        //To avoid numeric errors with lambda values higher than 1
        //a trynarization is performed instead
        if(lam>1 || lam <-1){
            if(q<q0)return 0;
            if(q==q0)return q0;
            if(q>q0)return L-1;
        }
        
        
        //True definition of the function
        if(lam==0)return q;//
        
        double r=(Math.exp( lam*(p1-p2) )-Math.exp( -lam*(p1-p2) ) );
        r=r*(   Math.exp( lam*(q-q0) )-Math.exp( -lam*(q-q0) )  );
        r=r/( GCcoef(lam,q0,p1,p2)*Math.exp( lam*(q-q0) )+GCcoef(-lam,q0,p1,p2)*Math.exp( -lam*(q-q0) ) );
        r=r+q0;       
        return r;
    }
    
    
    @Override
    public void run(String arg) {
        ImagePlus imp = WindowManager.getCurrentImage();
        if (imp==null) {
            IJ.error("No image", "Please select an image first");
            return;
        }
        else if (!imp.isRGB()) {
            IJ.error("Format not supported", "This plugin only works with RGB images.");
            return;
        }
        
        
        CustomCanvas cc = new CustomCanvas(imp);
        new CustomWindow(imp, cc);
        

        cc.requestFocus();
    }


    class CustomCanvas extends ImageCanvas {
    
        CustomCanvas(ImagePlus imp) {
            super(imp);
        }
        
        @Override
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
            //IJ.log("mousePressed: ("+offScreenX(e.getX())+","+offScreenY(e.getY())+")");
        }
    
    } // CustomCanvas inner class
    
    
    class CustomWindow extends ImageWindow implements ActionListener {
    
        //Widgets declarations
        private Button buttonUpdate, buttonInvert, buttonLoadConfig;  
        ImageProcessor ip;
        int qPixelsCopy[];
        int rPixels[];
        int WIDTH;
        int HEIGHT;
        
        CustomPlot cp = new CustomPlot("Transformation","pixel q","pixel r");
        ColorPanel redPanel;
        ColorPanel greenPanel;
        ColorPanel bluePanel;
       
        CustomWindow(ImagePlus imp, ImageCanvas ic) {
            super(imp, ic);
            setLayout(new FlowLayout());
            ip = imp.getProcessor(); 
            qPixelsCopy = (int[])ip.getPixelsCopy();
            rPixels = (int[])ip.getPixels();
            WIDTH = ip.getWidth();
            HEIGHT= ip.getHeight();
            addPanel();
        }
        
        //ModTanhCeen function variables intialization

    
        void addPanel() {
            //Create panel and layout constraints
            Panel mainPanel = new Panel();
            mainPanel.setSize(300, 550);
            mainPanel.setLayout(new GridLayout(0,1));
            mainPanel.setBackground(Color.white);
            
            //Add color panels
            redPanel = new ColorPanel("Red Channel:");
            mainPanel.add(redPanel);
            greenPanel = new ColorPanel("Green Channel:");
            mainPanel.add(greenPanel);
            bluePanel = new ColorPanel("Blue Channel:");
            mainPanel.add(bluePanel);
            
            //Add ScrollBars functionality for lambda
            redPanel.LambdaScroll.addAdjustmentListener(new AdjustmentListener() { 
                @Override
                public void adjustmentValueChanged(AdjustmentEvent e) {    
                   redPanel.LambdaText.setText(String.valueOf( redPanel.LambdaScroll.getValue()*1.0/10000  )  ); 
                   cp.setRedTransform(redPanel.get_q0(), redPanel.get_Lambda(), redPanel.get_P1(),redPanel.get_P2());
                   updateImagePixels();
                   imp.updateAndDraw();
                }    
            });
            
            greenPanel.LambdaScroll.addAdjustmentListener(new AdjustmentListener() {  
                @Override
                public void adjustmentValueChanged(AdjustmentEvent e) {    
                   greenPanel.LambdaText.setText(String.valueOf( greenPanel.LambdaScroll.getValue()*1.0/10000  )  ); 
                   cp.setGreenTransform(greenPanel.get_q0(), greenPanel.get_Lambda(), greenPanel.get_P1(),greenPanel.get_P2());
                   updateImagePixels();
                   imp.updateAndDraw();
                }    
            });
            
            bluePanel.LambdaScroll.addAdjustmentListener(new AdjustmentListener() { 
                @Override
                public void adjustmentValueChanged(AdjustmentEvent e) {    
                   bluePanel.LambdaText.setText(String.valueOf( bluePanel.LambdaScroll.getValue()*1.0/10000  )  ); 
                   cp.setBlueTransform(bluePanel.get_q0(), bluePanel.get_Lambda(), bluePanel.get_P1(),bluePanel.get_P2());
                   updateImagePixels();
                   imp.updateAndDraw();
                }    
            });
            //Add ScrollBars functionality for q0
            redPanel.q0Scroll.addAdjustmentListener(new AdjustmentListener() { 
                @Override
                public void adjustmentValueChanged(AdjustmentEvent e) {    
                   redPanel.q0Text.setText(String.valueOf( redPanel.q0Scroll.getValue()  )  ); 
                   cp.setRedTransform(redPanel.get_q0(), redPanel.get_Lambda(), redPanel.get_P1(),redPanel.get_P2());
                   updateImagePixels();
                   imp.updateAndDraw();
                }    
            });
            
            greenPanel.q0Scroll.addAdjustmentListener(new AdjustmentListener() { 
                @Override
                public void adjustmentValueChanged(AdjustmentEvent e) {    
                   greenPanel.q0Text.setText(String.valueOf( greenPanel.q0Scroll.getValue()  )  ); 
                   cp.setGreenTransform(greenPanel.get_q0(), greenPanel.get_Lambda(), greenPanel.get_P1(),greenPanel.get_P2());
                   updateImagePixels();
                   imp.updateAndDraw();
                }    
            });
            
            bluePanel.q0Scroll.addAdjustmentListener(new AdjustmentListener() { 
                @Override
                public void adjustmentValueChanged(AdjustmentEvent e) {    
                   bluePanel.q0Text.setText(String.valueOf( bluePanel.q0Scroll.getValue()  )  ); 
                   cp.setBlueTransform(bluePanel.get_q0(), bluePanel.get_Lambda(), bluePanel.get_P1(),bluePanel.get_P2());
                   updateImagePixels();
                   imp.updateAndDraw();
                }    
            });
                        
                        

            //Add update button and LoadConfig with their panel
            Panel buttonPanel = new Panel();
            buttonPanel.setLayout(new GridLayout(0,3));
            buttonPanel.setPreferredSize(new Dimension(300,30));
            
            buttonUpdate = new Button(" Update ");
            buttonUpdate.addActionListener(this);
            buttonPanel.add(buttonUpdate);

            buttonInvert = new Button(" Invert ");
            buttonInvert.addActionListener(this);
            buttonPanel.add(buttonInvert);
            
            buttonLoadConfig = new Button("Load Config");
            buttonLoadConfig.addActionListener(this);
            buttonPanel.add(buttonLoadConfig);
            
            mainPanel.add(buttonPanel);
            //panel.setSize(400, 800);
            add(mainPanel);
            pack();
        }
        
        public void updateScrollbarValues(){
            redPanel.q0Scroll.setValue(redPanel.get_q0());
            redPanel.LambdaScroll.setValue( (int)( redPanel.get_Lambda()*10000 ));
            greenPanel.q0Scroll.setValue(greenPanel.get_q0());
            greenPanel.LambdaScroll.setValue( (int)( greenPanel.get_Lambda()*10000 ));
            bluePanel.q0Scroll.setValue(bluePanel.get_q0());
            bluePanel.LambdaScroll.setValue( (int)( bluePanel.get_Lambda()*10000 ));
        }
        
        public void setParametersOnPlot(){
            cp.setRedTransform(redPanel.get_q0(), redPanel.get_Lambda(), redPanel.get_P1(),redPanel.get_P2());
            cp.setGreenTransform(greenPanel.get_q0(), greenPanel.get_Lambda(), greenPanel.get_P1(),greenPanel.get_P2());
            cp.setBlueTransform(bluePanel.get_q0(), bluePanel.get_Lambda(), bluePanel.get_P1(),bluePanel.get_P2());
            cp.show();
        }
        
        public void updateImagePixels(){
            int redPixel;
            int greenPixel;
            int bluePixel;
            
            for(int a=0;a<WIDTH*HEIGHT;a++){
                //take the red, green and blue values of the original image
                redPixel = (int) (qPixelsCopy[a] & 0xff0000)>>16; 
                greenPixel = (int) (qPixelsCopy[a] & 0x00ff00)>>8; 
                bluePixel = (int) (qPixelsCopy[a] & 0x0000ff); 
                
                //apply the transformations to those pixel values
                redPixel = (int) (redChannelTransf[redPixel]);
                greenPixel = (int) (greenChannelTransf[greenPixel]);
                bluePixel = (int) (blueChannelTransf[bluePixel]);
                
                rPixels[a]= ((redPixel & 0xff)<<16)+((greenPixel & 0xff)<<8) + (bluePixel & 0xff);
                
            }
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            Object b = e.getSource();
            if (b==buttonUpdate) {
                this.setParametersOnPlot();
                this.updateScrollbarValues();
                this.updateImagePixels();
                imp.updateAndDraw();
            } 
            if (b==buttonInvert){
                imp.getProcessor().invert();
                imp.updateAndDraw();
            }
            if (b==buttonLoadConfig){
                GenericDialog gd = new GenericDialog("Config");
                gd.addChoice("Config", configs, configs[0]);
                gd.showDialog();
                String choice=gd.getNextChoice();
                //Load predetermined configurations
                if(gd.wasOKed()){
                    if(choice.equals(configs[0])){
                        redPanel.setConfiguration("128", "0", "255", "0.0001");
                        greenPanel.setConfiguration("128", "0", "255", "0.0001");
                        bluePanel.setConfiguration("128", "0", "255", "0.0001");
                    }
                    else if(choice.equals(configs[1]) ){
                        redPanel.setConfiguration("0", "-100", "255", "0.05");
                        greenPanel.setConfiguration("0", "-100", "255", "0.05");
                        bluePanel.setConfiguration("0", "-100", "255", "0.05");
                    }
                    else if(choice.equals(configs[2])){
                        redPanel.setConfiguration("255", "0", "355", "0.05");
                        greenPanel.setConfiguration("255", "0", "355", "0.05");
                        bluePanel.setConfiguration("255", "0", "355", "0.05");
                    }
                }
                this.setParametersOnPlot();
                this.updateImagePixels();
                imp.updateAndDraw();
                
            }
                
            ImageCanvas ic = imp.getCanvas();
            if (ic!=null)
                ic.requestFocus();
        }
        
    } // CustomWindow inner class

   class CustomPlot extends Plot{
       
       CustomPlot(String title, String xLabel, String yLabel){
           super(title,xLabel,yLabel);
           this.setLimits(-10, 260, -10, 260);
	   this.setSize(400, 400);
           this.setLineWidth(2);
           intializeVariables();
           createReferenceLine();
           this.setColor(Color.red);
           this.addPoints(qValues, redChannelTransf, LINE);
           this.setColor(Color.green);
           this.addPoints(qValues, greenChannelTransf, LINE);
           this.setColor(Color.blue);
           this.addPoints(qValues, blueChannelTransf, LINE);
           updateAll();
           this.show();
           
       }//CustomPlot constructor
       
       
       void intializeVariables(){
           for(int a=0;a< L;a++)
                {
                    redChannelTransf[a]= GModTanhCeen(a,0.0001,128,0,255);
                    greenChannelTransf[a]= GModTanhCeen(a,0.0001,128,0,255);
                    blueChannelTransf[a]= GModTanhCeen(a,0.0001,128,0,255);
                    qValues[a]=a;
                }
       }
           
       public void updateRed(){
           this.setColor(Color.red);
           this.replace(1,"Line",qValues,  redChannelTransf);
       }
       
       public void updateGreen(){
           this.setColor(Color.green);
           this.replace(2,"Line", qValues, greenChannelTransf);
       }
       
       public void updateBlue(){
           this.setColor(Color.blue);
           this.replace(3,"Line",qValues, blueChannelTransf);
       }
       
       void createReferenceLine(){
           this.setColor(Color.black);
           this.addPoints(qValues, qValues, LINE);
       }
       
       public void updateAll(){
           updateRed();
           updateGreen();
           updateBlue();
       }
       
       public void setRedTransform(int q0, double lam, int p1, int p2){
           for(int a=0;a< L;a++)
                {
                    redChannelTransf[a]= GModTanhCeen(a,lam,q0,p1,p2);
                }
           updateRed();
       }
       
       public void setGreenTransform(int q0, double lam, int p1, int p2){
           for(int a=0;a< L;a++)
                {
                    greenChannelTransf[a]= GModTanhCeen(a,lam,q0,p1,p2);
                }
           updateGreen();
       }
       
       public void setBlueTransform(int q0, double lam, int p1, int p2){
           for(int a=0;a< L;a++)
                {
                    blueChannelTransf[a]= GModTanhCeen(a,lam,q0,p1,p2);
                }
           updateBlue();
       }
       
   }//CustomPlot inner class
   
   class ColorPanel extends Panel {
       
        private Label colorLabel;
        private TextField q0Text  = new TextField("128");
        private TextField LambdaText = new TextField("0.0001",7);
        private TextField P1Text = new TextField("0",3);
        private TextField P2Text = new TextField("255",3);
        private Scrollbar LambdaScroll = new Scrollbar(Scrollbar.HORIZONTAL,50,10,1,10000);
        private Scrollbar q0Scroll = new Scrollbar(Scrollbar.HORIZONTAL,128,10,1,254);
       
       ColorPanel(String panelName){
           super();
           setPreferredSize( new Dimension(300, 120));
           setLayout(new FlowLayout());
           this.setPanelName(panelName);
           this.addElements();
           this.setBackground(Color.LIGHT_GRAY);
           
       }
       
       void setPanelName(String panelName){
           colorLabel = new Label(panelName);
           colorLabel.setPreferredSize(new Dimension(300,20));
           this.add(this.colorLabel);
           
       }
       
       void addElements(){
           this.add(new Label("q0:"));
           this.add(q0Text);
           this.add(q0Scroll);
           this.add(new Label("P1:"));
           this.add(P1Text);
           this.add(new Label("P2:"));
           this.add(P2Text);
           this.add(new Label("Lambda:"));
           this.add(LambdaText);
           this.add(LambdaScroll);
           
           //Setting sizes for place adjustments
           q0Scroll.setPreferredSize(new Dimension(200,20));
           q0Scroll.setBackground(Color.white);
           
           LambdaScroll.setPreferredSize(new Dimension(300,20));
           LambdaScroll.setBackground(Color.white);
           
           P1Text.setColumns(2);
           P2Text.setColumns(2);
           LambdaText.setColumns(2);
           
           
           //
              
            
       }
       
       //Get parameters methods
       int get_q0(){
           return Integer.parseInt(q0Text.getText());
       }
       
       int get_P1(){
           return Integer.parseInt(P1Text.getText());
       }
       
       int get_P2(){
           return Integer.parseInt(P2Text.getText());
       }
       
       double get_Lambda(){
           return Double.parseDouble(LambdaText.getText());
       }
       
       void setConfiguration(String q0, String P1, String P2, String Lambda){
           this.q0Text.setText(q0);
           this.P1Text.setText(P1);
           this.P2Text.setText(P2);
           this.LambdaText.setText(Lambda);
           this.q0Scroll.setValue(Integer.parseInt(q0));
           this.LambdaScroll.setValue( (int)( Double.parseDouble(Lambda)*10000 ));
       }
   }

} // Color_Panel class