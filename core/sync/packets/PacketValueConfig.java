package appeng.core.sync.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import appeng.api.config.FuzzyMode;
import appeng.container.implementations.ContainerCellWorkbench;
import appeng.container.implementations.ContainerLevelEmitter;
import appeng.container.implementations.ContainerPriority;
import appeng.container.implementations.ContainerSecurity;
import appeng.core.sync.AppEngPacket;
import appeng.core.sync.network.INetworkInfo;

public class PacketValueConfig extends AppEngPacket
{

	final public String Name;
	final public String Value;

	// automatic.
	public PacketValueConfig(ByteBuf stream) throws IOException {
		DataInputStream dis = new DataInputStream( new ByteArrayInputStream( stream.array(), 0, stream.readableBytes() ) );
		Name = dis.readUTF();
		Value = dis.readUTF();
		dis.close();
	}

	@Override
	public void serverPacketData(INetworkInfo manager, AppEngPacket packet, EntityPlayer player)
	{
		Container c = player.openContainer;

		if ( Name.equals( "TileSecurity.ToggleOption" ) && c instanceof ContainerSecurity )
		{
			ContainerSecurity sc = (ContainerSecurity) c;
			sc.toggleSetting( Value, player );
			return;
		}
		else if ( Name.equals( "PriorityHost.Priority" ) && c instanceof ContainerPriority )
		{
			ContainerPriority pc = (ContainerPriority) c;
			pc.setPriority( Integer.parseInt( Value ), player );
			return;
		}
		else if ( Name.equals( "LevelEmitter.Value" ) && c instanceof ContainerLevelEmitter )
		{
			ContainerLevelEmitter lvc = (ContainerLevelEmitter) c;
			lvc.setLevel( Integer.parseInt( Value ), player );
			return;
		}
		else if ( Name.startsWith( "CellWorkbench." ) && c instanceof ContainerCellWorkbench )
		{
			ContainerCellWorkbench ccw = (ContainerCellWorkbench) c;
			if ( Name.equals( "CellWorkbench.Action" ) )
			{
				if ( Value.equals( "Partition" ) )
				{
					ccw.partition();
				}
				else if ( Value.equals( "Clear" ) )
				{
					ccw.clear();
				}
			}
			else if ( Name.equals( "CellWorkbench.Fuzzy" ) )
			{
				ccw.setFuzzy( FuzzyMode.valueOf( Value ) );
			}
		}

	}

	// api
	public PacketValueConfig(String Name, String Value) throws IOException {
		this.Name = Name;
		this.Value = Value;

		ByteBuf data = Unpooled.buffer();

		data.writeInt( getPacketID() );

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream( bos );
		data.writeBytes( Name.getBytes() );
		data.writeBytes( Value.getBytes() );
		dos.close();

		data.writeBytes( bos.toByteArray() );

		configureWrite( data );
	}
}
