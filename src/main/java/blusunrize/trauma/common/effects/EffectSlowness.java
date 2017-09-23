/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */
package blusunrize.trauma.common.effects;

import blusunrize.trauma.api.LimbCondition;
import blusunrize.trauma.api.effects.IEffectPotion;
import blusunrize.trauma.api.effects.PotionEffectMap;
import blusunrize.trauma.common.Trauma;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;

/**
 * @author BluSunrize
 * @since 23.09.2017
 */
public class EffectSlowness implements IEffectPotion
{
	@Override
	public String getIndentifier()
	{
		return Trauma.MODID+":Slowness";
	}

	@Override
	public String getDescription(EntityPlayer player, LimbCondition limbCondition)
	{
		return "desc.trauma.effect.slowness."+(limbCondition.getState().ordinal()-1);
	}

	@Override
	public void addToPotionMap(EntityPlayer player, LimbCondition limbCondition, PotionEffectMap map)
	{
		int mod = limbCondition.getState().ordinal();
		if(mod>=0)
			map.modifyEffect(MobEffects.SLOWNESS, mod);
	}
}
