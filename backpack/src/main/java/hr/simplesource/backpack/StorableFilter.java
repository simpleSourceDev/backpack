package hr.simplesource.backpack;

public interface StorableFilter<T extends Storable> {
    boolean filter(final T storable);
}
