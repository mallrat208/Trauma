/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */
package blusunrize.trauma.common;

import blusunrize.trauma.api.*;
import blusunrize.trauma.api.effects.ITraumaEffect;
import blusunrize.trauma.common.damageadapters.DamageAdapterFall;
import blusunrize.trauma.common.effects.*;
import blusunrize.trauma.common.utils.EventHandler;
import blusunrize.trauma.common.utils.commands.CommandTrauma;
import blusunrize.trauma.common.utils.network.MessageTraumaStatusSync;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author BluSunrize
 * @since 20.09.2017
 */
@Mod(modid = Trauma.MODID, name = Trauma.MODNAME, version = Trauma.VERSION)
public class Trauma
{
	public static final String MODID = "trauma";
	public static final String MODNAME = "Trauma";
	public static final String VERSION = "0.9";

	@Mod.Instance(MODID)
	public static Trauma instance = new Trauma();
	@SidedProxy(clientSide = "blusunrize.trauma.client.ClientProxy", serverSide = "blusunrize.trauma.common.CommonProxy")
	public static CommonProxy proxy;

	public static final SimpleNetworkWrapper packetHandler = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		proxy.preInit();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init();
		TraumaConfig.loadConfig();
		CapabilityTrauma.register();
		MinecraftForge.EVENT_BUS.register(new EventHandler());

		int messageId = 0;
		packetHandler.registerMessage(MessageTraumaStatusSync.HandlerServer.class, MessageTraumaStatusSync.class, messageId++, Side.SERVER);
		packetHandler.registerMessage(MessageTraumaStatusSync.HandlerClient.class, MessageTraumaStatusSync.class, messageId++, Side.CLIENT);

		/* Register Damage Handlers */
		IDamageAdapter adapter = new DamageAdapterFall();
		for(String fall : TraumaConfig.fallDamages)
			TraumaApiLib.registerDamageAdapter(fall, adapter);

		/* Init all the Effects */
		/*Chest*/
		ITraumaEffect effect = new EffectExhaustion();
		for(EnumTraumaState state : EnumTraumaState.DAMAGED_STATES)
			TraumaApiLib.registerEffect(EnumLimb.CHEST, state, effect);
		/*Arms*/
		effect = new EffectMining();
		ITraumaEffect effect2 = new EffectAttackSpeed();
		for(EnumTraumaState state : EnumTraumaState.DAMAGED_STATES)
		{
			TraumaApiLib.registerEffect(EnumLimb.ARM_MAIN, state, effect);
			TraumaApiLib.registerEffect(EnumLimb.ARM_MAIN, state, effect2);
		}
		/*Legs*/
		effect = new EffectSlowness();
		for(EnumTraumaState state : EnumTraumaState.DAMAGED_STATES)
		{
			TraumaApiLib.registerEffect(EnumLimb.LEG_LEFT, state, effect);
			TraumaApiLib.registerEffect(EnumLimb.LEG_RIGHT, state, effect);
		}
		effect = new EffectNoJump();
		for(EnumTraumaState state : EnumTraumaState.EQUAL_OR_WORSE_STATES.get(EnumTraumaState.MEDIUM))
		{
			TraumaApiLib.registerEffect(EnumLimb.LEG_LEFT, state, effect);
			TraumaApiLib.registerEffect(EnumLimb.LEG_RIGHT, state, effect);
		}
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit();
	}

	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandTrauma());
	}

}
