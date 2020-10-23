package com.ecommerce.microcommerce.web.controller;

import com.ecommerce.microcommerce.dao.ProductDao;
import com.ecommerce.microcommerce.model.Product;
import com.ecommerce.microcommerce.web.exceptions.ProduitIntrouvableException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ProductController {

    @Autowired
    private ProductDao productDao;


    //Récupérer la liste des produits
    @RequestMapping(value = "/Produits", method = RequestMethod.GET)
    public MappingJacksonValue listeProduits() {
        Iterable<Product> produits = productDao.findAll();
        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");
        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);
        MappingJacksonValue produitsFiltres = new MappingJacksonValue(produits);
        produitsFiltres.setFilters(listDeNosFiltres);
        return produitsFiltres;
    }


    //Récupérer un produit par son Id
    @GetMapping(value = "/Produit/{productid}")
    public Product afficherUnProduit(@PathVariable int productid) {
        return productDao.findById(productid);
    }





    //ajouter un produit
    @PostMapping(value = "/Produit")
    public ResponseEntity<Void> ajouterProduit(@Valid @RequestBody Product product) {

        Product productAdded =  productDao.save(product);

        if (productAdded == null)
            return ResponseEntity.noContent().build();

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productAdded.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    // supprimer un produit
    @DeleteMapping(value ="/Produit/{productid}")
    public void supprimerProduit(@PathVariable int productid) {productDao.delete(productid);
    }

    // Mettre à jour un produit
    @PutMapping (value = "/Produit")
    public void updateProduit(@RequestBody Product product) {
         productDao.chercherUnProduitCher(400);
    }


    //Pour les tests
    @GetMapping(value = "test/produits/{prix}")
    public List<Product>  testeDeRequetes(@PathVariable int prix) {
        return productDao.chercherUnProduitCher(400);
    }



   // calculer la marge produit
   @RequestMapping(value = "/AdminProduits", method = RequestMethod.GET)
   public Map<String, Integer> calculerMargeProduit() {
       Map<String, Integer> result = new HashMap<String, Integer>();
       List<Product> productList = productDao.findAll();
       for (Product p : productList) {
           result.put(p.toString(), p.getPrix() - p.getPrixAchat());
       }
       return result;
   }
    //Tri par ordre alphabétique
    @RequestMapping(value = "/ProduitsOrderByName", method = RequestMethod.GET)
    public List<Product> trierProduitsParOrdreAlphabetique() {
        return productDao.findByOrderByNomAsc();
    }

}
