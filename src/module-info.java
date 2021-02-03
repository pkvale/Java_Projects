public class JavaPractice2_1_21 {
	    public static void main(String args[]) {
	    	//1.1
	        System.out.println("Welcome to java!" + "\n");
	        System.out.println("Welcome to computer science!" + "\n");
	        System.out.println("Programming is fun!" + "\n");



	        //1.2)
	        for (int i=0; i<5; i++){
	            System.out.println("Welcome to java " + i);
	        }
	   
	        //1.3)
            System.out.println("    J     A    V    V    A");
            System.out.println("    J    A A    V  V    A A");
            System.out.println("J   J   AAAAA    VV    AAAAA");
            System.out.println(" J J   A     A    V   A     A");
	
            //1.4)
            System.out.println("    a       a^2     a^3");
            System.out.println("    1       1       1");
            System.out.println("    2       4       8");
            System.out.println("    3       9       27");
            System.out.println("    4       16      64");
	
            //1.5)
	        double myNum = ((9.5*4.5)-(2.5*3))/(45.5-3.5);
	        System.out.println(myNum);

	        //1.6)
	        int myNumber = 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9;
	        System.out.println(myNumber);
	  
	        //1.7)
	        double myDouble = 1;
	        for (int i=1; i<50000; i++){
	            double denominator = ((double)i * 2) + 1;
	            if (i%2 == 0){
	                myDouble += 1/denominator;
	            }
	            else {
	                myDouble -= 1/denominator;
	            }
	        }
	        myDouble = 4 * myDouble;
	        System.out.println(myDouble);
	   
	        //1.8)
	        double pi = 3.1415927;
	        double radius = 5.5;
	        double circumference= 2 * radius * pi;
	        System.out.println ("Perimeter: " + circumference);
	        double area = radius * radius * pi;
	        System.out.println ("Area: " + area);
	   
	        //1.9)
	        double width = 4.5;
	        double height = 7.9;
	        double perimeter = 2 * width + 2 * height;
	        System.out.println ("Perimeter: " + perimeter);
	        double areaRectangle = width * height;
	        System.out.println ("Area: " + areaRectangle);
	   
	        //1.10)
	        double speedInKPM = 14/45.5;
	        double speedInMPH = speedInKPM * (1/1.6)* (60/1);
	        System.out.println ("Speed in MPH: " + speedInMPH);
	   
	        //1.11)
	        double currentUSPopulation = 312032486;
	        //  sec * 60/min * 60/hr * 24/day * 365 / yr /people
	        double birthsPerYear = 60 * 60 * 24 * 365 / 7;
	        double deathsPerYear =  60 * 60 * 24 * 365 / 13;
	        double immigrationsPerYear =  60 * 60 * 24 * 365 / 45;
	        System.out.println ("People born:" + birthsPerYear + ", People died:" + deathsPerYear + ", People Immigrated" + immigrationsPerYear);
	        currentUSPopulation = currentUSPopulation + birthsPerYear - deathsPerYear + immigrationsPerYear;
	        System.out.println("Current US Population is: " + currentUSPopulation);
	   
	        //1.12)
	        //find avg speed in KPH-->MPH = 24/1.66666666
	        double time = 1 + (40.0/60.0) + (35 / (60 * 60));
	        double speedSTD = 24/time;
	        double speedMetric = speedSTD * 1.6;
	        System.out.println("The runner's speed in KPH is: " + speedMetric);
	  
	        //1.13)
	        //solve 2 linear EQs using cramer's rule
	        double a = 3.4, b = 50.2, c = 2.1, d = 0.55, e = 44.5, f = 5.9; 
	        double x = ((e * d) - (b * f)) / ((a * d) - (b * c));
	        double y = ((a * f) - (e * c)) / ((a * d) - (b * c));
	        System.out.println("The value for x is: " + x + ", The value for y is: " + y);
	    }
	}