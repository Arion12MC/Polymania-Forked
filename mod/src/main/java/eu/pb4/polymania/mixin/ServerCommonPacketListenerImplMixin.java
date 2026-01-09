package eu.pb4.polymania.mixin;

import eu.pb4.polymania.dialog.PolymaniaDialogs;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundShowDialogPacket;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonPacketListenerImpl.class)
public abstract class ServerCommonNetworkHandlerMixin {
    @Shadow public abstract void send(Packet<?> packet);

    @Shadow @Final protected MinecraftServer server;

    @Inject(method = "handleCustomClickAction", at = @At("TAIL"))
    private void handleCustomClick(ServerboundCustomClickActionPacket packet, CallbackInfo ci) {
        if (!packet.id().getNamespace().equals("polymania")) {
            return;
        }

        switch (packet.id().getPath()) {
            case "open/changelog" -> this.send(new ClientboundShowDialogPacket(Holder.direct(PolymaniaDialogs.getChangelog(
                    packet.payload().flatMap(x -> x.asString().map(Identifier::tryParse)).flatMap(this.server.registryAccess().lookupOrThrow(Registries.DIALOG)::get)
            ))));
            case "open/mods" -> this.send(new ClientboundShowDialogPacket(Holder.direct(PolymaniaDialogs.getModList(
                    packet.payload().flatMap(x -> x.asString().map(Identifier::tryParse)).flatMap(this.server.registryAccess().lookupOrThrow(Registries.DIALOG)::get),
                    packet.payload().flatMap(x -> x.asString().map(Identifier::tryParse))
            ))));
            case "open/mod_page" -> {
                try {
                    var nbt = (CompoundTag) packet.payload().orElseThrow();
                    var mod = nbt.getStringOr("mod", "polymania-extras");
                    var previous = nbt.getString("prev");
                    this.send(new ClientboundShowDialogPacket(Holder.direct(PolymaniaDialogs.getModPage(mod, previous))));
                } catch (Throwable e) {
                    // ignore
                }
            }

        }
    }
}
