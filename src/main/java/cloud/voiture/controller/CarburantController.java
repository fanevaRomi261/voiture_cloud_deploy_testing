package cloud.voiture.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloud.voiture.model.Carburant;
import cloud.voiture.model.ResponseWrap;
import cloud.voiture.repository.CarburantRepository;

@RestController
@RequestMapping("/admin/carburant")
public class CarburantController {
    
    @Autowired
    CarburantRepository carburantRepository;
    
    @GetMapping("/")
    public ResponseEntity<ResponseWrap<List<Carburant>>> getAllCarburant() {
        return new ResponseEntity<>(ResponseWrap.success(carburantRepository.findAll()),HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseWrap<Carburant> getCarburantById(@PathVariable long id) {
        return carburantRepository.findById(id).map(carburant -> ResponseWrap.success(carburant))
                .orElseGet(() -> ResponseWrap.error("Carburant non trouvee"));
    }

    @DeleteMapping("/{id}")
    public void deleteCarburant(@PathVariable long id) {
        carburantRepository.deleteById(id);
    }

    @PostMapping("/")
    public Carburant createCarburant(@RequestBody Carburant carburant) {
        return carburantRepository.save(carburant);
    }

    @PutMapping("/{id}")
    public ResponseWrap<Carburant> updateCarburant(@RequestBody Carburant carburant, @PathVariable int id)
            throws Exception {
        if (carburantRepository.existsById((long) id)) {
            carburant.setId((long) id);
            return ResponseWrap.success(carburantRepository.saveAndFlush(carburant));
        }else {
            return ResponseWrap.error("Le type de carburant n'existe pas");
        }
    }

}
