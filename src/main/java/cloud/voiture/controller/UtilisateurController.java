package cloud.voiture.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloud.voiture.model.ResponseWrap;
import cloud.voiture.model.Utilisateur;
import cloud.voiture.repository.UtilisateurRepository;

@RestController
@RequestMapping("/utilisateur")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UtilisateurController {

    @Autowired
    UtilisateurRepository utilisateurRepository;

    
    @GetMapping("/")
    public ResponseWrap<List<Utilisateur>> getAllUtilisateurs() {
        return ResponseWrap.success(utilisateurRepository.findByActif(1));
    }

    @GetMapping("/{id}")
    public ResponseWrap<Utilisateur> getUtilisateurById(@PathVariable long id) {
        return utilisateurRepository.findById((int)id).map(utilisateur -> ResponseWrap.success(utilisateur))
                .orElseGet(() -> ResponseWrap.error("utilisateur non trouvee"));
    }

    @DeleteMapping("/{id}")
    public void deleteUtilisateur(@PathVariable long id) {
        Utilisateur utilisateur = utilisateurRepository.getById((int)id);
        utilisateur.setActif(0);
        utilisateurRepository.saveAndFlush(utilisateur);
    }

    @PostMapping("/")
    public Utilisateur createUtilisateur(@RequestBody Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    @PutMapping("/{id}")
    public ResponseWrap<Utilisateur> updateUtilisateur(@RequestBody Utilisateur utilisateur, @PathVariable int id)
            throws Exception {
        if (utilisateurRepository.existsById(id)) {
            utilisateur.setId(id);
            return ResponseWrap.success(utilisateurRepository.saveAndFlush(utilisateur));
        }else {
            return ResponseWrap.error("utilisateur n'existe pas");
        }
    }

}
