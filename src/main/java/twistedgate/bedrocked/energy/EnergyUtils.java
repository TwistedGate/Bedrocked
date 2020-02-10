package twistedgate.bedrocked.energy;

import java.util.Locale;

public class EnergyUtils{
	public static String toString(int power, boolean usePost){
		if(power>=1000000000)
			return String.format(Locale.ENGLISH, usePost?"%.1fGRF":"%.1f", power/(float)1000000000);
		
		else if(power>=1000000)
			return String.format(Locale.ENGLISH, usePost?"%.1fMRF":"%.1f", power/(float)1000000);
		
		else if(power>=1000)
			return String.format(Locale.ENGLISH, usePost?"%.1fKRF":"%.1f", power/(float)1000);
		
		else
			return power+(usePost?"RF":"");
	}
}
