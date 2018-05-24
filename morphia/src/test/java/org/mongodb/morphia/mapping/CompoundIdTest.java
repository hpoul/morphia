package org.mongodb.morphia.mapping;


import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.mongodb.morphia.AdvancedDatastore;
import org.mongodb.morphia.TestBase;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.annotations.Version;

import java.io.Serializable;


public class CompoundIdTest extends TestBase {

    @Test
    public void testDelete() {
        final CompoundIdEntity entity = new CompoundIdEntity();
        entity.id = new CompoundId("test");

        getDatastore().save(entity);
        getDatastore().deleteOne(CompoundIdEntity.class, entity.id);
    }

    @Test
    public void testFetchKey() {
        getDatastore().save(new ConfigEntry(new ConfigKey("env", "key", "subenv")));
        ConfigEntry entry = getDatastore().find(ConfigEntry.class).get();
        entry.setValue("something");
        getDatastore().save(entry);
    }

    @Test
    public void testMapping() {
        CompoundIdEntity entity = new CompoundIdEntity();
        entity.id = new CompoundId("test");

        getDatastore().save(entity);
        entity = getDatastore().get(entity);
        Assert.assertEquals("test", entity.id.name);
        Assert.assertNotNull(entity.id.id);
    }

    @Test
    public void testOtherDelete() {
        final CompoundIdEntity entity = new CompoundIdEntity();
        entity.id = new CompoundId("test");

        getDatastore().save(entity);
        final String collectionName = getDatastore().getCollection(CompoundIdEntity.class).getNamespace().getCollectionName();
        ((AdvancedDatastore) getDatastore()).deleteOne(collectionName, CompoundIdEntity.class, entity.id);
    }

    @Test
    public void testReference() {
        getMorphia().map(CompoundIdEntity.class, CompoundId.class);
        getDatastore().getCollection(CompoundIdEntity.class).drop();

        final CompoundIdEntity sibling = new CompoundIdEntity();
        sibling.id = new CompoundId("sibling ID");
        getDatastore().save(sibling);

        final CompoundIdEntity entity = new CompoundIdEntity();
        entity.id = new CompoundId("entity ID");
        entity.e = "some value";
        entity.sibling = sibling;
        getDatastore().save(entity);

        final CompoundIdEntity loaded = getDatastore().get(entity);
        Assert.assertEquals(entity, loaded);
    }

    @Embedded
    private static class CompoundId {
        private final ObjectId id = new ObjectId();
        private String name;

        CompoundId() {
        }

        CompoundId(final String n) {
            name = n;
        }

        @Override
        public int hashCode() {
            int result = id.hashCode();
            result = 31 * result + name.hashCode();
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof CompoundId)) {
                return false;
            }
            final CompoundId other = ((CompoundId) obj);
            return other.id.equals(id) && other.name.equals(name);
        }

    }

    private static class CompoundIdEntity {
        @Id
        private CompoundId id;
        private String e;
        @Reference
        private CompoundIdEntity sibling;

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final CompoundIdEntity that = (CompoundIdEntity) o;

            if (!id.equals(that.id)) {
                return false;
            }
            if (e != null ? !e.equals(that.e) : that.e != null) {
                return false;
            }
            return !(sibling != null ? !sibling.equals(that.sibling) : that.sibling != null);

        }

        @Override
        public int hashCode() {
            int result = id.hashCode();
            result = 31 * result + (e != null ? e.hashCode() : 0);
            result = 31 * result + (sibling != null ? sibling.hashCode() : 0);
            return result;
        }
    }

    public static class ConfigKey {
        private String env;
        private String subenv;
        private String key;

        public ConfigKey() {
        }

        public ConfigKey(final String env, final String key, final String subenv) {
            this.env = env;
            this.key = key;
            this.subenv = subenv;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final ConfigKey configKey = (ConfigKey) o;

            if (!env.equals(configKey.env)) {
                return false;
            }
            if (!subenv.equals(configKey.subenv)) {
                return false;
            }
            return key.equals(configKey.key);

        }

        @Override
        public int hashCode() {
            int result = env.hashCode();
            result = 31 * result + subenv.hashCode();
            result = 31 * result + key.hashCode();
            return result;
        }
    }

    @Entity(useDiscriminator = false)
    public static class ConfigEntry {
        @Id
        private ConfigKey key;
        private String value;
        @Version
        private long version;
        private String lastModifiedUser;
        private long lastModifiedMillis;

        public ConfigEntry() {
        }

        public ConfigEntry(final ConfigKey key) {
            this.key = key;
        }

        public ConfigKey getKey() {
            return key;
        }

        public void setKey(final ConfigKey key) {
            this.key = key;
        }

        public long getLastModifiedMillis() {
            return lastModifiedMillis;
        }

        public void setLastModifiedMillis(final long lastModifiedMillis) {
            this.lastModifiedMillis = lastModifiedMillis;
        }

        public String getLastModifiedUser() {
            return lastModifiedUser;
        }

        public void setLastModifiedUser(final String lastModifiedUser) {
            this.lastModifiedUser = lastModifiedUser;
        }

        public String getValue() {
            return value;
        }

        public void setValue(final String value) {
            this.value = value;
        }

        public long getVersion() {
            return version;
        }

        public void setVersion(final long version) {
            this.version = version;
        }
    }
}
