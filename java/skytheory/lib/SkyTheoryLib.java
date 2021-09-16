package skytheory.lib;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import skytheory.lib.event.CapabilityEvent;
import skytheory.lib.event.TickTimeEvent;
import skytheory.lib.event.WrenchEvent;
import skytheory.lib.init.proxy.CommonProxy;
import skytheory.lib.util.SkyTheoryLibLogger;

/**
 * 自分でModを製作する際に、後からコードを（自分が）読んで理解できるようにするためのライブラリ
 *
 * @author SkyTheory
 *
 */
@Mod(
		modid = SkyTheoryLib.MOD_ID,
		name = SkyTheoryLib.MOD_NAME,
		version = SkyTheoryLib.VERSION,
		guiFactory = "skytheory.lib.config.ConfigGuiFactory"
	)
public class SkyTheoryLib {

	public static final String MOD_ID = "stlib";
	public static final String MOD_NAME = "SkyTheoryLib";
	public static final String MC_VERSION = "1.12.2";
	public static final String MOD_VERSION = "1.2.0";
	public static final String VERSION = MC_VERSION + "-" + MOD_VERSION;
	public static final SkyTheoryLibLogger LOGGER = SkyTheoryLibLogger.getLogger();

	/*
	 * プロキシ基本三点セット
	 * インターフェースを作ってもいいし、サブクラスという形でもいい
	 * 重要なのはクライアント側とサーバー側で互換性のある実体を渡せること
	 */
//	public static final String PROXY_CLIENT = "examplemod.init.proxy.ClientProxy";
//	public static final String PROXY_SERVER = "examplemod.init.proxy.ServerProxy";
//	@SidedProxy(clientSide = PROXY_CLIENT, serverSide = PROXY_SERVER)
//	public IProxy proxy;

	public static final String PROXY_COMMON = "skytheory.lib.init.proxy.CommonProxy";
	@SidedProxy(clientSide = PROXY_COMMON, serverSide = PROXY_COMMON)
	public static CommonProxy proxy;

	/**
	 * 何らかの理由でMod本体のインスタンスが必要になった場合、{@code @Mod.Instance}を付けた変数から持ってくること<br>
	 * Forge側から初期化を行うため、Mod側からの変数の初期化の必要はない
	 */
	@Mod.Instance
	public static SkyTheoryLib INSTANCE;

	/**
	 * この形でEVENT_BUS（イベントを運ぶ貨物車両のようなもの）に登録しておけば<br>
	 * 必要に応じてそこに書かれたイベントが実行される
	 * @param event
	 */
	@Mod.EventHandler
	public void construct(FMLConstructionEvent event) {
//		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(CapabilityEvent.class);
		MinecraftForge.EVENT_BUS.register(TickTimeEvent.class);
		MinecraftForge.EVENT_BUS.register(WrenchEvent.class);
	}
	/*
	 * 以下に実装例を示しておく
	 * メソッドの名前や変数名は適宜変更すること
	 *
	 * ・EVENT_BUSに登録したインスタンスの持つメソッドである
	 * ・SubscribeEventのアノテーションがある
	 * ・publicかつvoidで宣言されている
	 * ・引数が特定のイベントひとつのみである（+適切な総称型が設定されている）
	 *
	 * 以上の条件を満たせば引数のイベントのタイミングで実行される
	 * EVENT_BUSへの登録はクラスでも良い、その場合はstaticでEventを宣言すること
	 *
	 */
	/*

	@SubscribeEvent
	public void registerRegistries(RegistryEvent.NewRegistry event) {
		ResourceRegister.createRegistry<ExampleAttribute>(ExampleMod.MOD_ID, "ExampleName");
	}

	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		ResourceRegister.register(event, ExampleBlocks.class);
	}

	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event) {
		ResourceRegister.register(event), ExampleItems.class);
	}

	@SubscribeEvent
	public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		ResourceRegister.register(event.getRegistry(), ExampleEntityEntries.class);
	}

	@SubscribeEvent
	public void registerExample(RegistryEvent.Register<ExampleAttribute> event) {
		ResourceRegister.register(event, ExampleValues.class);
	}

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		this.proxy.modelRegister();
	}

	 */
	/*
	 * イベントのタイミングで実行される
	 * こちらはEVENT_BUSへの登録が不要な代わりに@Modのアノテーションが必要
	 * アノテーションが@Mod.EventHandlerに変わっている点は注意
	 * 以下に実装例を示しておく
	 */

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

}
