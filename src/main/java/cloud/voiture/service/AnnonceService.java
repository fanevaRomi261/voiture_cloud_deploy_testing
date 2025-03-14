package cloud.voiture.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cloud.voiture.authentification.JwtUtil;
import cloud.voiture.model.Annonce;
import cloud.voiture.model.Historique;
import cloud.voiture.model.Photo;
import cloud.voiture.repository.AnnonceRepository;
import cloud.voiture.repository.HistoriqueRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class AnnonceService {

    @Autowired
    private AnnonceRepository annonceRepository;

    @Autowired
    private HistoriqueRepository historiqueRepository;

    @Autowired
    private ImageUploadService imageUploadService;

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private JwtUtil jwtUtil;

    /*
     * 
     * PUBLIC METHOD
     * 
     */

    // pour l'accueil
    public List<Annonce> getAnnoncesValidee() {
        return annonceRepository.findByEtat(5);
    }

    public int getIdUtilisateurFromJwt(HttpServletRequest request) throws Exception{
        Claims claims = jwtUtil.resolveClaims(request);
        // System.out.println("CLAIMS = "+claims.toString());
        if (claims != null) {
            return (int) claims.get("iduser");
        } else {
            throw new RuntimeException("Utilisateur non authentifié");
        }
    }

    /*
     * ADMIN METHODS
     */
    public List<Annonce> getAllAnnonce() {
        return annonceRepository.findAll();
    }

    public Optional<Annonce> getAnnonceById(String id) {
        return annonceRepository.findById(id);
    }

    public List<Annonce> getAnnonceNonValidee() {
        return annonceRepository.findByEtat(0);
    }

    public Annonce validerAnnonce(String id) throws Exception {
        Annonce annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new Exception("id not found"));
        if (annonce.getEtat() != 0) {
            throw new Exception("annonce deja accepter ou supprimer");
        }
        annonce.setEtat(5);
        annonceRepository.save(annonce);

        return annonce;
    }

    public Annonce refuserAnnonce(String id) throws Exception {
        Annonce annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new Exception("id not found"));
        if (annonce.getEtat() != 0) {
            throw new Exception("annonce deja refuser ou supprimer");
        }
        annonce.setEtat(-5);
        annonceRepository.save(annonce);

        return annonce;
    }

    /*
     * USER METHODS
     */

    // accueil rehefa connecter le olona de ze validee sy ze annonce tsy anazy no alaina
    public List<Annonce> getAccueilConnectee(int iduser) {
        return annonceRepository.findByUtilisateurIdNotAndEtat(iduser, 5);
    }

    // historique de ses annonces
    // rehefa mijery ny historique any le olona de asina etat mitsy le affichage hoe ito efa valider de ito mbola na hoe vendu sy supprimer
    public List<Annonce> getMesAnnonces(int iduser) {
        return annonceRepository.findByUtilisateurId(iduser);
    }

    public Annonce saveAnnonce(Annonce nouvelleAnnonce,String[] imageBase64) throws Exception{

        List<Photo> urls = imageUploadService.getUrl(imageBase64, nouvelleAnnonce.getUtilisateur().getId());
        nouvelleAnnonce.setPhoto(urls);
        return annonceRepository.save(nouvelleAnnonce);
    }

    public Annonce changeStatusToSold(int idvendeur,int idacheteur,String idannonce) throws Exception{

        Optional<Annonce> concerned = annonceRepository.findById(idannonce);

        if(concerned.isEmpty()){
            throw new Exception("annonce inexistante");
        }
        if(concerned.get().getEtat() != 5){
            throw new Exception("l'etat de l'annonce ne permet pas de la vendre (supprimee/non validee/vendu/)");
        }

        Historique historique = new Historique();
        historique.setIdacheteur(idacheteur);
        historique.setIdvendeur(idvendeur);
        historique.setIdannonce(idannonce);
        historique.setDate(new Date());

        double prixAnnonce = concerned.get().getPrix();
        double commissionValeur = prixAnnonce * commissionService.getRecentCommission(prixAnnonce).getValeur() / 100;
        historique.setCommission(commissionValeur);

        concerned.get().setEtat(10);
        annonceRepository.save(concerned.get());

        historiqueRepository.save(historique);
        return concerned.get();
    }

    


}
