package bitwize.nullawesome;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class EntityRepositoryTest {

    private class TestComponent {
	public int value;
    }


    // Test that we can fetch the singleton instance of the
    // EntityRepository.
    @Test
    public void repositoryExists() {
	assertNotNull(EntityRepository.get());
    }

    private int insertTestComponent() {
	TestComponent tc = new TestComponent();
	int eid;
	try {
	    eid = EntityRepository.get().newEntity(); 
	    tc.value = 39;
	    EntityRepository.get().addComponent(eid, tc);
	}
	catch(Exception e) { throw new RuntimeException(e); } 
	return eid;
    }
    
    // Test insertion and retrieval of a component.
    @Test
    public void componentInsertionAndRetrieval() {
	EntityRepository.createInstance();
	TestComponent tc;
	int eid = insertTestComponent();
	try { tc = (TestComponent)EntityRepository.get().getComponent(eid, TestComponent.class); }
	catch(Exception e) { throw new RuntimeException(e); }
	assertEquals(tc.value, 39);
    }

    // Test that repository returns null upon retrieval of invalid EID.
    @Test
    public void fetchInvalidEid() {
	EntityRepository.createInstance();
	TestComponent tc = (TestComponent)EntityRepository.get().getComponent(42,TestComponent.class);
	assertTrue(tc == null);
    }

    // Test that repository barfs upon attempted add to invalid EID.
    @Test(expected=InvalidEntityException.class)
    public void addToInvalidEid() throws InvalidEntityException {
	EntityRepository.createInstance();
	TestComponent tc = new TestComponent();
	tc.value = 39;
	EntityRepository.get().addComponent(42, tc);
    }

    @Test
    public void removeEntities() throws EntityTableFullException {
	EntityRepository.createInstance();
	for(int i=0; i<42; i++) {
	    EntityRepository.get().newEntity();
	}
	assertTrue(EntityRepository.get().hasEntity(25));
	EntityRepository.get().removeEntity(25);
	assertFalse(EntityRepository.get().hasEntity(25));
    }



    // Test filling the entity table.
    @Test(expected=EntityTableFullException.class)
    public void fullEntityTable() throws EntityTableFullException {
	EntityRepository.createInstance();
	// first fill the entity table to the rim; the try/catch here
	// will assert that the table gets filled without overflowing
	// and raising an EntityTableFullException
	try { 
	    for(int i=0;i<EntityRepository.MAX_ENTITIES;i++) {
		EntityRepository.get().newEntity();
	    }
	}
	catch(EntityTableFullException e) {
	    // wrap the exception before throwing so test will fail
	    throw new RuntimeException(e);
	}
	// now add one more entity; this SHOULD fail
	EntityRepository.get().newEntity();
    }

    // Test that "holes" in the entity table are properly filled once
    // the current entity no. goes past MAX_ENTITIES.
    @Test(expected=EntityTableFullException.class)
    public void entityAllocWrapsAround() throws EntityTableFullException {
    	EntityRepository.createInstance();
    	int test = 1048;
    	// first fill the entity table to the rim; the try/catch here
    	// will assert that the table gets filled without overflowing
    	// and raising an EntityTableFullException
    	try { 
    	    for(int i=0;i<EntityRepository.MAX_ENTITIES;i++) {
    		EntityRepository.get().newEntity();
    	    }
    	}
    	catch(EntityTableFullException e) {
    	    // wrap the exception before throwing so test will fail
    	    throw new RuntimeException(e);
    	}
	EntityRepository.get().removeEntity(test);
	int newEid = EntityRepository.get().newEntity();
	assertEquals(newEid, test);
	EntityRepository.get().newEntity();
    }

    @Test
    public void entityFinding() throws EntityTableFullException {
    	EntityRepository.createInstance();
	int test = 75;
	for(int i=0; i<test; i++) {
	    EntityRepository.get().newEntity();
	}
	int i = insertTestComponent();
	int j = EntityRepository.get().findEntityWithComponent(TestComponent.class);
	assertEquals(j, i);
    }

    @Test
    public void entityProcessing() throws EntityTableFullException {
    	EntityRepository.createInstance();
	EntityRepository repo = EntityRepository.get();
	int ent0 = repo.newEntity();
	int ent1 = repo.newEntity();
	int ent2 = repo.newEntity();
	int ent3 = repo.newEntity();
	TestComponent tc1 = new TestComponent();
	TestComponent tc2 = new TestComponent();
    }

}
