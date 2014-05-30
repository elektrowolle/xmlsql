package com.roman.ppaper.helpers;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.roman.ppaper.helpers.xmlsql.DataSet;
import com.roman.ppaper.helpers.xmlsql.EntityView;

//Repraesentiert einen Charakter. Dient als Schnittstelle
//Zwischen Datenbank und Programm.
public class GameCharacter extends EntityView{	
	private static final String   ENTITY_NAME = "character";
	public  static final String   ENTITY_DATA_XML 
		= "characters_data.xml";
	public  static final String[] CONTENT_DATA_XML = {
		"characters_abilities.xml"
		,"characters_weapons.xml"
		,"characters_myths.xml"};

	/**
	 * @param context
	 * @param id
	 * -1 für einen neuen Charakter
	 */
	public GameCharacter(Context context, int id) {
		super(ENTITY_DATA_XML, CONTENT_DATA_XML, ENTITY_NAME, id, context);
	}
	
	public GameCharacter(Context context, DataSet set) {
		super(ENTITY_DATA_XML, CONTENT_DATA_XML, ENTITY_NAME, set, context);
	}
	
	//TODO: NullChecks
	
	//Manche Werte des Charakters werden aus anderen generiert.
	//Hier stehen die Methoden dafuer, diese werden bei der
	//Charaktererstellung ausgefuehrt.
	public Integer generateLivePoints () {
		Integer constituition = getAsInteger("constitution");
		Integer height        = getAsInteger("height");
		int livePoints = (constituition + height) / 2;
		set("hit_points", livePoints);
		return livePoints;
	}
	
	public Integer generateMentalStability(){
		
		Integer mental_stability = getAsInteger("mana");
		if (mental_stability<15) mental_stability=15;
		else if (mental_stability>90) mental_stability=90;
		set("mental_stability",mental_stability);
		return mental_stability;
	}
	
	public Integer generateIdea(){
		
		int idea = getAsInteger("intelligence");
		if (idea<40) idea=40;
		else if (idea>90) idea=90;
		set("idea", idea);
		return idea;
		
	}
	
	public Integer generateLuck(){
		
		int luck = getAsInteger("mana");
		if (luck<15) luck=15;
		else if (luck>90) luck=90;
		set("luck", luck);
		return luck;
		
	}
	
	public Integer generateKnowledge(){
		
		int knowledge = getAsInteger("education");
		if (knowledge<15) knowledge=15;
		else if (knowledge>90) knowledge=90;
		set("knowledge",knowledge);
		return knowledge;	
	}
	
	public String generateDamageBonus(){
		
		Integer sum = getAsInteger("strength")+getAsInteger("height");
		
		if(sum >= 2 && sum <=12 ){
		set("damage_bonus","-1W6");
		return "-1W6";
		}
		else if(sum >= 13 && sum <= 16 ){
		set("damage_bonus","-1W4");
		return "-1W4";
		}
		else if(sum >= 17 && sum <= 24 ){
		set("damage_bonus","keiner");
		return "keiner";
		}
		else if(sum >= 25 && sum <= 32 ){
		set("damage_bonus","+1W4");
		return "+1W4";
		}
		else if(sum >= 33 && sum <= 40 ){
		set("damage_bonus","+1W6");
		return "+1W6";
		}
		else if(sum >= 41 && sum <= 56 ){
		set("damage_bonus","+2W6");
		return "+2W6";
		}
		else if(sum >= 57 && sum <= 72 ){
		set("damage_bonus","+3W6");
		return "+3W6";
		}
		else if(sum >= 73 && sum <= 88 ){
		set("damage_bonus","+4W6");
		return "+4W6";
		}
		else if(sum > 88){
		sum = sum - 88;
		int wuerfelzahl = (sum / 16) + 4;
		String result = "+"+ wuerfelzahl +"W6";
		set("damage_bonus",result);
		Log.d("boring",result);
		return result;
		}
		
		return "";
		
	}
	
	public static void charaktereAusgeben(Context context){
		ArrayList<DataSet> selectAll = EntityView.getManager(ENTITY_DATA_XML, CONTENT_DATA_XML, context).selectAll(ENTITY_NAME, null);

		String output = "";
		for (DataSet characterSet : selectAll){
			
			String tempString = characterSet.getAsString("name")+" "+characterSet.getAsString("character_id")+"\n";
			
			output = output + tempString;
			
		}
		Log.d("GameCharacter",output);
		
	}
	
	public static ArrayList<DataSet> getCharacters(Context context){
		ArrayList<DataSet> selectAll = EntityView.getManager(ENTITY_DATA_XML, CONTENT_DATA_XML, context).selectAll(ENTITY_NAME, null);

		
		Log.d("Character", "All of them: " + selectAll);
		
		
		//TODO: Die Datenbank wird hier gelöscht!
		return selectAll;
	}
}
