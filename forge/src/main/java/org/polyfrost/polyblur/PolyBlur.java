package org.polyfrost.polyblur;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.client.ClientCommandHandler;

@Mod(
        modid = PolyBlur.ID,
        name = PolyBlur.NAME,
        version = Tags.VERSION,
        acceptableRemoteVersions = "*",
        guiFactory = "org.polyfrost.polyblur.PolyBlurGuiFactory"
)
public class PolyBlur {
    public static final String ID = "polyblur";
    public static final String NAME = "PolyBlur";

    private boolean openGui;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        PolyBlurConfig.load(event.getSuggestedConfigurationFile());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (!event.getSide().isClient()) return;
        FMLCommonHandler.instance().bus().register(this);
        ClientCommandHandler.instance.registerCommand(new CommandBase() {
            @Override
            public String getCommandName() {
                return ID;
            }

            @Override
            public String getCommandUsage(ICommandSender sender) {
                return "/" + ID;
            }

            @Override
            public boolean canCommandSenderUseCommand(ICommandSender sender) {
                return true;
            }

            @Override
            public void processCommand(ICommandSender sender, String[] args) {
                openGui = true;
            }
        });
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !openGui) return;
        openGui = false;
        Minecraft.getMinecraft().displayGuiScreen(new PolyBlurGui(null));
    }
}
