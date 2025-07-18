package tn.esprit.spring.kaddem.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.kaddem.entities.Contrat;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.entities.Specialite;
import tn.esprit.spring.kaddem.repositories.ContratRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContratServiceImplTest {

    @Mock
    private ContratRepository contratRepository;
    
    @Mock
    private EtudiantRepository etudiantRepository;
    
    @InjectMocks
    private ContratServiceImpl contratService;
    
    private Contrat contrat;
    private Etudiant etudiant;
    private Date startDate;
    private Date endDate;
    
    @BeforeEach
    void setUp() {
        // Setup dates
        Calendar calendar = Calendar.getInstance();
        calendar.set(2025, Calendar.JANUARY, 1);
        startDate = calendar.getTime();
        
        calendar.set(2025, Calendar.DECEMBER, 31);
        endDate = calendar.getTime();
        
        // Setup contrat
        contrat = new Contrat();
        contrat.setIdContrat(1);
        contrat.setDateDebutContrat(startDate);
        contrat.setDateFinContrat(endDate);
        contrat.setSpecialite(Specialite.IA);
        contrat.setArchive(false);
        
        // Setup etudiant
        etudiant = new Etudiant();
        etudiant.setIdEtudiant(1);
        etudiant.setNomE("Test");
        etudiant.setPrenomE("Student");
        etudiant.setContrats(new HashSet<>());
    }
    
    @Test
    void testRetrieveAllContrats() {
        // Given
        List<Contrat> contrats = new ArrayList<>();
        contrats.add(contrat);
        when(contratRepository.findAll()).thenReturn(contrats);
        
        // When
        List<Contrat> result = contratService.retrieveAllContrats();
        
        // Then
        assertEquals(1, result.size());
        verify(contratRepository, times(1)).findAll();
    }
    
    @Test
    void testAddContrat() {
        // Given
        when(contratRepository.save(any(Contrat.class))).thenReturn(contrat);
        
        // When
        Contrat savedContrat = contratService.addContrat(contrat);
        
        // Then
        assertNotNull(savedContrat);
        assertEquals(1, savedContrat.getIdContrat());
        verify(contratRepository, times(1)).save(contrat);
    }
    
    @Test
    void testUpdateContrat() {
        // Given
        contrat.setSpecialite(Specialite.CLOUD);
        when(contratRepository.save(any(Contrat.class))).thenReturn(contrat);
        
        // When
        Contrat updatedContrat = contratService.updateContrat(contrat);
        
        // Then
        assertNotNull(updatedContrat);
        assertEquals(Specialite.CLOUD, updatedContrat.getSpecialite());
        verify(contratRepository, times(1)).save(contrat);
    }
    
    @Test
    void testRetrieveContrat() {
        // Given
        when(contratRepository.findById(1)).thenReturn(Optional.of(contrat));
        
        // When
        Contrat foundContrat = contratService.retrieveContrat(1);
        
        // Then
        assertNotNull(foundContrat);
        assertEquals(1, foundContrat.getIdContrat());
        verify(contratRepository, times(1)).findById(1);
    }
    
    @Test
    void testRemoveContrat() {
        // Given
        when(contratRepository.findById(1)).thenReturn(Optional.of(contrat));
        
        // When
        contratService.removeContrat(1);
        
        // Then
        verify(contratRepository, times(1)).findById(1);
        verify(contratRepository, times(1)).delete(contrat);
    }
    
    @Test
    void testAffectContratToEtudiant() {
        // Given
        when(etudiantRepository.findByNomEAndPrenomE("Test", "Student")).thenReturn(etudiant);
        when(contratRepository.findByIdContrat(1)).thenReturn(contrat);
        when(contratRepository.save(any(Contrat.class))).thenReturn(contrat);
        
        // When
        Contrat result = contratService.affectContratToEtudiant(1, "Test", "Student");
        
        // Then
        assertNotNull(result);
        assertEquals(etudiant, result.getEtudiant());
        verify(etudiantRepository, times(1)).findByNomEAndPrenomE("Test", "Student");
        verify(contratRepository, times(1)).findByIdContrat(1);
        verify(contratRepository, times(1)).save(contrat);
    }
}
