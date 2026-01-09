package eu.pb4.polymania.mixin.client;

import com.google.common.collect.Multiset;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;

@Mixin(SelectItemModelProperty.Type.class)
public class SelectPropertyTypeMixin {
    @Redirect(method = "validateCases", at = @At(value = "INVOKE", target = "Ljava/util/Set;size()I"))
    private static int replaceSize(Set instance, @Local Multiset multiset) {
        return multiset.size();
    }
}
