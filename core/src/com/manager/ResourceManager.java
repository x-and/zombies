package com.manager;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation2D;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.minlog.Log;
import com.zombie.SoundInfo;
import com.zombie.util.XMLUtils;

public class ResourceManager extends AssetManager {

	static Map<String,TextureAtlas> atlases = new ConcurrentHashMap<String,TextureAtlas>(); 
	static Map<String,TextureRegion> textures = new ConcurrentHashMap<String,TextureRegion>(); 
	static Map<String,Animation2D> animations = new ConcurrentHashMap<String,Animation2D>(); 
	static Map<String,SoundInfo> sounds = new ConcurrentHashMap<String,SoundInfo>(); 
	static Map<String,ParticleEffect> emitters = new ConcurrentHashMap<String,ParticleEffect>(); 
	static Map<String,ShaderProgram> shaders = new ConcurrentHashMap<String,ShaderProgram>(); 

	public static boolean loaded = false;
	public static int totalResources = 0;
	public static int loadedResources = 0;
	public static int resourceIdx;
	static NodeList listResources;
	
	private static ResourceManager instance = new ResourceManager();
	
	public static ResourceManager getInstance(){
		return instance;
	}

	public void init(boolean test) {
		loadResources(Gdx.files.internal("data/data/resources.xml").read());
		Gdx.app.log("INFO", "ResourceManager init");
	}
	
	public static void loadResources(InputStream is){
		Document doc = XMLUtils.getDocumentForStream(is);
        doc.getDocumentElement ().normalize ();
        listResources = doc.getElementsByTagName("resource");
        totalResources = listResources.getLength();
	}
	
	public static void loadResources(float delta){
		int current = resourceIdx+5;
		if (current >= totalResources)
			current = totalResources;
        for(;resourceIdx < current; resourceIdx++){
        	Node resourceNode = listResources.item(resourceIdx);
        	if(resourceNode.getNodeType() == Node.ELEMENT_NODE){
        		Element resourceElement = (Element)resourceNode;
         			addResource(resourceElement);
        	}
        	loadedResources++;
        }
        
		if (resourceIdx >= totalResources)
			endLoad();
	}
	
	
	private static void endLoad() {
        loaded = true;
        listResources = null;
        Log.info("ResourceManager", "Loaded");
        Log.info("ResourceManager", "    Sprites: "+textures.size());
        Log.info("ResourceManager", "    Animations: "+animations.size());
        Log.info("ResourceManager", "    Sounds: "+sounds.size());
        Log.info("ResourceManager", "    Emitters: "+emitters.size());
        Log.info("ResourceManager", "    Shaders: "+shaders.size());
		System.gc();
	}
	
	private static void addResource(Element resourceElement) {
		String type = resourceElement.getAttribute("type");
		if(type.equals("atlas")){
			addElementAsAtlas(resourceElement);
		} else if(type.equals("image")){
			addElementAsImage(resourceElement);
		}else if(type.equals("animation")){
			addElementAsAnimation(resourceElement);
		}else if(type.equals("sound")){
			addElementAsSound(resourceElement);
		}else if(type.equals("sprite")){
			addElementAsSprite(resourceElement);
		}else if(type.equals("emitter")){
			addElementAsParticleEmitter(resourceElement);
		}else if(type.equals("shader")){
			addElementAsShader(resourceElement);
		}
	}
	
	private static void addElementAsShader(Element el) {
		ShaderProgram.pedantic = false;
		ShaderProgram shader = new ShaderProgram(Gdx.files.internal(el.getTextContent()+el.getAttribute("id")+".vsh"),
				Gdx.files.internal(el.getTextContent()+el.getAttribute("id")+".fsh"));
		if (shader.isCompiled())
			shaders.put(el.getAttribute("id"), shader);
	}

	private static void addElementAsParticleEmitter(Element el) {
		loadParticleEmitter(el.getAttribute("id"), el.getAttribute("sprite"), el.getTextContent());
	}

	private static void loadParticleEmitter(String ID,String spriteID, String path) {
		ParticleEffect emitter = new ParticleEffect();
		emitter.loadEmitters(Gdx.files.internal(path));
		emitter.loadEmitterImages(atlases.get("atlas"));

		emitters.put(ID, emitter);
	}

	private static void addElementAsSprite(Element el) {
		 loadSprite(el.getAttribute("id"), el.getTextContent());
	}
	 
	public static TextureRegion loadSprite(String id, String path){
		Texture texture = new Texture(Gdx.files.internal(path));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		TextureRegion image = new TextureRegion(texture);
		textures.put(id, image);
		return image;
	}

	public static ParticleEffect getEmitter(String ID){
		return emitters.get(ID);
	}
	
	private static void addElementAsAtlas(Element el) {
//		getInstance().load(resourceElement.getTextContent(), TextureAtlas.class);
		loadAtlas(el.getAttribute("id"), el.getTextContent());
	}

	private static void loadAtlas(String id, String path) {
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(path));
		atlases.put(id, atlas);
	}

	public static void addImage(Sprite image, String ID) {
		 textures.put(ID, image);
	 }
	 
	private static final void addElementAsImage(Element el){
		loadImage(el.getAttribute("id"), el.getTextContent());
	}

	public static TextureRegion loadImage(String id, String path){
		TextureAtlas atlas = atlases.get("atlas");
		TextureRegion image = null;
		image = atlas.findRegion(path);
		textures.put(id, image);
		return image;
	}

	public static final TextureRegion getImage(String ID){
		TextureRegion img = textures.get(ID);
		return img;
	}
	
	private static void addElementAsAnimation(Element el){
		try {
			loadAnimation(el.getAttribute("id"), el.getTextContent(),
					Integer.valueOf(el.getAttribute("x")),
					Integer.valueOf(el.getAttribute("y")),
					Integer.valueOf(el.getAttribute("tw")),
					Integer.valueOf(el.getAttribute("th")),
					Integer.valueOf(el.getAttribute("duration")),
					Integer.valueOf(el.getAttribute("frames"))
					);
		} catch (NumberFormatException e) {

			e.printStackTrace();
		} catch (DOMException e) {

			e.printStackTrace();
		}
	}
 
	private static void loadAnimation(String id, String spriteSheetPath,
			int x, int y, int tw, int th, int duration, int frames){

		TextureAtlas atlas = atlases.get("atlas");
		TextureRegion image = null;
		image = atlas.findRegion(spriteSheetPath);
		Array<TextureRegion> array = new Array<TextureRegion>();
		int count = 0;
		for(int xx = x/tw; xx < image.getRegionWidth()/tw;xx++){
			for(int yy = y/th; yy < image.getRegionHeight()/th;yy++){
				if (frames != 0 && count == frames)
					break;
				if (frames != 0) count++;
				TextureRegion reg = new TextureRegion(image);
				reg.setRegion(image.getRegionX()+xx*tw, image.getRegionY()+yy*th, tw, th);
				array.add(reg);
			}
		}	 
		Animation2D animation = new Animation2D(duration,array);
		animation.setPlayMode(Animation.LOOP);
		animations.put(id,animation);
	}
 
	public static final Animation2D getAnimation(String ID){
		return animations.get(ID);
	}
	
	private static void addElementAsSound(final Element el){
		ThreadPoolManager.getInstance().executeTask(new Runnable(){
			@Override
			public void run() {
				SoundInfo info = new SoundInfo();
				Sound sound = Gdx.audio.newSound(Gdx.files.internal(el.getTextContent()));
				info.sound = sound;
				info.radius = Float.parseFloat(el.getAttribute("radius"));
				sounds.put(el.getAttribute("id"), info);
			}});
	}

	public static final SoundInfo getSound(String ID){
		return sounds.get(ID);
	}

	public static boolean isLoaded() {
		return loaded;
	}

	public static ShaderProgram getShader(String string) {
		return shaders.get(string);
	}


	public static void stopAllSounds() {
		for( SoundInfo s : sounds.values())
			s.sound.stop();
	}

}
