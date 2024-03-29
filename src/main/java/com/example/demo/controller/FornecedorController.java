package com.example.demo.controller;

import com.example.demo.entity.Cargo;
import com.example.demo.entity.Fornecedor;
import com.example.demo.entity.Representante;
import com.example.demo.entity.Users;
import com.example.demo.repository.CargoRepository;
import com.example.demo.repository.FornecedorRepository;
import com.example.demo.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/")
public class FornecedorController {

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private CargoRepository cargoRepository;

    @Autowired
    private UsersRepository usersRepository;

    @GetMapping("")
    public ModelAndView viewHomePage() {
        ModelAndView mav = new ModelAndView("index");
        return mav;
    }

    @GetMapping("list")
    public ModelAndView getAllFornecedor() {
        ModelAndView mav = new ModelAndView("list-fornecedor");
        List<Fornecedor> list = fornecedorRepository.findAll();
        mav.addObject("fornecedor", list);
        return mav;
    }

    @GetMapping("fornecedor")
    public ModelAndView addFornecedor() {
        ModelAndView mav = new ModelAndView("add-fornecedor-form");
        Fornecedor newFornecedor = new Fornecedor();
        newFornecedor.getRepresentante().add(new Representante());
        mav.addObject("fornecedor", newFornecedor);
        return mav;
    }

    @RequestMapping(value = "fornecedor", params = {"save"})
    private void saveFornecedor(@ModelAttribute Fornecedor fornecedor, HttpServletResponse response) throws IOException {
        fornecedorRepository.save(fornecedor);
        response.sendRedirect("list");
    }

    @GetMapping("update")
    public ModelAndView showUpdateForm(@RequestParam Long fornecedorId) {
        ModelAndView mav = new ModelAndView("add-fornecedor-form");
        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId).get();
        mav.addObject("fornecedor", fornecedor);
        return mav;
    }

    @GetMapping("del")
    public void deleteFornecedor(@RequestParam Long fornecedorId, HttpServletResponse response) throws IOException {
        fornecedorRepository.deleteById(fornecedorId);
        response.sendRedirect("list");
    }

    @RequestMapping(value = "fornecedor", params = {"addRepresentante"})
    public ModelAndView addRepresentante(Fornecedor fornecedor) {
        ModelAndView mav = new ModelAndView("add-fornecedor-form");
        fornecedor.getRepresentante().add(new Representante());
        return mav;

    }

    @RequestMapping(value = "fornecedor", params = {"removeRepresentante"})
    public ModelAndView removeRepresentante(Fornecedor fornecedor, HttpServletRequest req) {
        final Integer representanteID = Integer.valueOf(req.getParameter("removeRepresentante"));
        fornecedor.getRepresentante().remove(representanteID.intValue());
        ModelAndView mav = new ModelAndView("add-fornecedor-form");
        mav.addObject(fornecedor);
        return mav;
    }

    @ModelAttribute("allCargos")
    public List<Cargo> populateCargo() {
        return cargoRepository.findAll();
    }

    @GetMapping("register")
    private ModelAndView showSignUpForm() {
        Users user = new Users();
        ModelAndView mav = new ModelAndView("register-form");
        mav.addObject("user", user);

        return mav;
    }

    @PostMapping("process_register")
    public ModelAndView processRegister(Users user) {
        ModelAndView mav = new ModelAndView("register-form");
        mav.addObject("user", user);
        if (usersRepository.findByEmail(user.getEmail()) == null) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            usersRepository.save(user);
            mav = new ModelAndView("register_success");
        }
        return mav;
    }
}
