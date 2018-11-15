package io.github.redpanda4552.PandaCard.MemoryCard.File;

public class IndirectFAT {

    private final int ENTRIES_PER_PAGE = 128, IFAT_PAGES = 2;
    
    private int[] iFAT = new int[IFAT_PAGES * ENTRIES_PER_PAGE];
    
    public IndirectFAT(FileMemoryCardPageFAT[] table) {
        for (int i = 0; i < table.length; i++) {
            FileMemoryCardPageFAT page = table[i];
            int[] entries = page.getFAT();
            
            for (int j = 0; j < entries.length; j++) {
                iFAT[(i * ENTRIES_PER_PAGE) + j] = entries[j];
            }
        }
    }
    
    public int[] getIFAT() {
        return iFAT;
    }
}
