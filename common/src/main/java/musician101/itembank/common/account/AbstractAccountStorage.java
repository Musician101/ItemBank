package musician101.itembank.common.account;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractAccountStorage<Page extends AbstractAccountPage, Player, World>
{
    protected File storageDir;
    protected Map<UUID, List<Page>> accountPages = new HashMap<>();

    protected AbstractAccountStorage(File storageDir)
    {
        this.storageDir = storageDir;
    }

    public File getFile(UUID uuid)
    {
        File file = new File(storageDir, uuid.toString() + ".itembank");
        if (!file.exists())
        {
            try
            {
                if (!file.createNewFile())
                    return null;
            }
            catch (IOException e)
            {
                return null;
            }
        }

        return file;
    }

    public List<File> resetAll()
    {
        List<File> files = new ArrayList<>();
        //noinspection ConstantConditions
        for (File file : storageDir.listFiles())
            if (!file.delete())
                files.add(file);

        return files;
    }

    public abstract boolean openInv(Player viewer, UUID owner, World world, int page);

    protected abstract void loadPages();
}
