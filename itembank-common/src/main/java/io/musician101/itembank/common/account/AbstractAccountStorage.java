package io.musician101.itembank.common.account;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//TODO load into memory
public abstract class AbstractAccountStorage<A extends AbstractAccountPage, P, W> {

    protected final Map<UUID, List<A>> accountPages = new HashMap<>();
    protected final File storageDir;

    protected AbstractAccountStorage(File storageDir) {
        this.storageDir = storageDir;
    }

    public File getFile(UUID uuid) {
        File file = new File(storageDir, uuid.toString() + ".itembank");
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    return null;
                }
            }
            catch (IOException e) {
                return null;
            }
        }

        return file;
    }

    protected abstract void loadPages();

    public abstract boolean openInv(P viewer, UUID owner, W world, int page);

    public List<File> resetAll() {
        List<File> files = new ArrayList<>();
        for (File file : storageDir.listFiles()) {
            if (!file.delete()) {
                files.add(file);
            }
        }

        return files;
    }
}
