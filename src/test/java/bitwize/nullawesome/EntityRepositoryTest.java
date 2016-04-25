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

    // Test that repository barfs upon retrieval of invalid EID.
    @Test(expected=InvalidEntityException.class)
    public void fetchInvalidEid() throws InvalidEntityException {
	EntityRepository.createInstance();
	EntityRepository.get().getComponent(42,TestComponent.class);
    }

    // Test that repository barfs upon attempted add to invalid EID.
    @Test(expected=InvalidEntityException.class)
    public void addToInvalidEid() throws InvalidEntityException {
	EntityRepository.createInstance();
	TestComponent tc = new TestComponent();
	tc.value = 39;
	EntityRepository.get().addComponent(42, tc);
    }

}
