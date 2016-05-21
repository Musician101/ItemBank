package io.musician101.itembank.common.account;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractAccountStorage<A extends AbstractAccountPage, P, W>
{
    protected final File storageDir;
    protected final Map<UUID, List<A>> accountPages = new HashMap<>();

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
            catch (IOException e)//NOSONAR
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

    public abstract boolean openInv(P viewer, UUID owner, W world, int page);

    protected abstract void loadPages();
}
