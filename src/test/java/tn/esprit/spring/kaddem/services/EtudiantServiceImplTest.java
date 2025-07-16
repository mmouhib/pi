package tn.esprit.spring.kaddem.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.kaddem.entities.Contrat;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Equipe;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.repositories.ContratRepository;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;
import tn.esprit.spring.kaddem.repositories.EquipeRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EtudiantServiceImplTest {

    @Mock
    private EtudiantRepository etudiantRepository;
    
    @Mock
    private DepartementRepository departementRepository;
    
    @Mock
    private ContratRepository contratRepository;
    
    @Mock
    private EquipeRepository equipeRepository;
    
    @InjectMocks
    private EtudiantServiceImpl etudiantService;
    
    private Etudiant etudiant;
    private Departement departement;
    private Contrat contrat;
    private Equipe equipe;
    
    @BeforeEach
    void setUp() {
        etudiant = new Etudiant();
        etudiant.setIdEtudiant(1);
        etudiant.setNomE("Test");
        etudiant.setPrenomE("Student");
        
        departement = new Departement();
        departement.setIdDepart(1);
        departement.setNomDepart("Computer Science");
        
        contrat = new Contrat();
        contrat.setIdContrat(1);
        
        equipe = new Equipe();
        equipe.setIdEquipe(1);
        equipe.setEtudiants(new HashSet<>());
    }
    
    @Test
    void testRetrieveAllEtudiants() {
        // Given
        List<Etudiant> etudiants = new ArrayList<>();
        etudiants.add(etudiant);
        when(etudiantRepository.findAll()).thenReturn(etudiants);
        
        // When
        List<Etudiant> result = etudiantService.retrieveAllEtudiants();
        
        // Then
        assertEquals(1, result.size());
        verify(etudiantRepository, times(1)).findAll();
    }
    
    @Test
    void testAddEtudiant() {
        // Given
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(etudiant);
        
        // When
        Etudiant savedEtudiant = etudiantService.addEtudiant(etudiant);
        
        // Then
        assertNotNull(savedEtudiant);
        assertEquals("Test", savedEtudiant.getNomE());
        verify(etudiantRepository, times(1)).save(etudiant);
    }
    
    @Test
    void testUpdateEtudiant() {
        // Given
        etudiant.setNomE("Updated");
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(etudiant);
        
        // When
        Etudiant updatedEtudiant = etudiantService.updateEtudiant(etudiant);
        
        // Then
        assertNotNull(updatedEtudiant);
        assertEquals("Updated", updatedEtudiant.getNomE());
        verify(etudiantRepository, times(1)).save(etudiant);
    }
    
    @Test
    void testRetrieveEtudiant() {
        // Given
        when(etudiantRepository.findById(1)).thenReturn(Optional.of(etudiant));
        
        // When
        Etudiant foundEtudiant = etudiantService.retrieveEtudiant(1);
        
        // Then
        assertNotNull(foundEtudiant);
        assertEquals(1, foundEtudiant.getIdEtudiant());
        verify(etudiantRepository, times(1)).findById(1);
    }
    
    @Test
    void testRemoveEtudiant() {
        // Given
        when(etudiantRepository.findById(1)).thenReturn(Optional.of(etudiant));
        
        // When
        etudiantService.removeEtudiant(1);
        
        // Then
        verify(etudiantRepository, times(1)).findById(1);
        verify(etudiantRepository, times(1)).delete(etudiant);
    }
    
    @Test
    void testAssignEtudiantToDepartement() {
        // Given
        when(etudiantRepository.findById(1)).thenReturn(Optional.of(etudiant));
        when(departementRepository.findById(1)).thenReturn(Optional.of(departement));
        
        // When
        etudiantService.assignEtudiantToDepartement(1, 1);
        
        // Then
        verify(etudiantRepository, times(1)).findById(1);
        verify(departementRepository, times(1)).findById(1);
        verify(etudiantRepository, times(1)).save(etudiant);
        assertEquals(departement, etudiant.getDepartement());
    }
    
    @Test
    void testAddAndAssignEtudiantToEquipeAndContract() {
        // Given
        when(contratRepository.findById(1)).thenReturn(Optional.of(contrat));
        when(equipeRepository.findById(1)).thenReturn(Optional.of(equipe));
        
        // When
        Etudiant result = etudiantService.addAndAssignEtudiantToEquipeAndContract(etudiant, 1, 1);
        
        // Then
        assertNotNull(result);
        assertEquals(etudiant, contrat.getEtudiant());
        assertTrue(equipe.getEtudiants().contains(etudiant));
    }
    
    @Test
    void testGetEtudiantsByDepartement() {
        // Given
        List<Etudiant> etudiants = new ArrayList<>();
        etudiants.add(etudiant);
        when(etudiantRepository.findEtudiantsByDepartement_IdDepart(1)).thenReturn(etudiants);
        
        // When
        List<Etudiant> result = etudiantService.getEtudiantsByDepartement(1);
        
        // Then
        assertEquals(1, result.size());
        verify(etudiantRepository, times(1)).findEtudiantsByDepartement_IdDepart(1);
    }
}
