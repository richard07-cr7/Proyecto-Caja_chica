package com.example.caja_chica.controller;

import com.example.caja_chica.model.*;
import com.example.caja_chica.repository.*;
import com.example.caja_chica.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ViewController {

    @Autowired private CajaChicaService cajaChicaService;
    @Autowired private GastoService gastoService;
    @Autowired private UsuarioService usuarioService;
    @Autowired private PresupuestoAreaService presupuestoAreaService;
    @Autowired private MovimientoService movimientoService;
    @Autowired private NotificacionService notificacionService;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private DepartamentoRepository departamentoRepository;
    @Autowired private PresupuestoAreaRepository presupuestoAreaRepository;
    @Autowired private CajaChicaRepository cajaChicaRepository;
    @Autowired private MovimientoRepository movimientoRepository;

    private boolean esAdmin(Authentication auth) {
        return auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    private Usuario getUsuarioActual(Authentication auth) {
        return usuarioRepository.findByUsername(auth.getName()).orElseThrow();
    }

    private void addNavModel(Model model, Authentication auth, String paginaActiva) {
        boolean admin = esAdmin(auth);
        Usuario usuario = getUsuarioActual(auth);
        long noLeidas = notificacionService.contarNoLeidas(usuario.getId());
        model.addAttribute("username", auth.getName());
        model.addAttribute("esAdmin", admin);
        model.addAttribute("paginaActiva", paginaActiva);
        model.addAttribute("notificacionesNoLeidas", noLeidas);
    }

    @GetMapping({"/", "/login"})
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        Model model) {
        if (error != null) model.addAttribute("error", "Usuario o contraseña incorrectos.");
        if (logout != null) model.addAttribute("logout", "Sesión cerrada correctamente.");
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        addNavModel(model, auth, "dashboard");
        boolean admin = esAdmin(auth);
        Usuario usuario = getUsuarioActual(auth);

        List<CajaChica> cajas = cajaChicaService.listar();
        if (!admin && usuario.getDepartamento() != null) {
            Long deptoId = usuario.getDepartamento().getId();
            cajas = cajas.stream()
                .filter(c -> c.getDepartamento() != null && c.getDepartamento().getId().equals(deptoId))
                .collect(Collectors.toList());
        }

        List<Gasto> gastos = admin ? gastoService.listarTodos() : List.of();
        long pendientes = gastos.stream().filter(g -> "PENDIENTE".equals(g.getEstado())).count();
        BigDecimal saldoTotal = cajas.stream()
            .map(c -> c.getSaldoActual() != null ? c.getSaldoActual() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("cajas", cajas);
        model.addAttribute("totalCajas", cajas.size());
        model.addAttribute("totalGastos", gastos.size());
        model.addAttribute("totalPendientes", pendientes);
        model.addAttribute("saldoTotal", saldoTotal);
        model.addAttribute("departamentos", admin ? departamentoRepository.findAll() : List.of());
        model.addAttribute("deptoNombre",
            usuario.getDepartamento() != null ? usuario.getDepartamento().getNombre() : "");
        return "dashboard";
    }

    @PostMapping("/dashboard/crear-caja")
    public String crearCaja(@RequestParam Long deptoId,
                            @RequestParam BigDecimal montoInicial,
                            RedirectAttributes ra) {
        try {
            cajaChicaService.crearCaja(montoInicial, deptoId);
            ra.addFlashAttribute("exito", "Caja creada exitosamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/gastos")
    public String gastos(Model model, Authentication auth) {
        addNavModel(model, auth, "gastos");
        boolean admin = esAdmin(auth);
        Usuario usuario = getUsuarioActual(auth);

        List<Gasto> gastos = admin
            ? gastoService.listarTodos()
            : gastoService.listarPorUsuario(usuario.getId());

        model.addAttribute("gastos", gastos);
        return "gastos";
    }

    @PostMapping("/gastos/{id}/aprobar")
    public String aprobarGasto(@PathVariable Long id, RedirectAttributes ra) {
        try {
            gastoService.aprobarGasto(id);
            ra.addFlashAttribute("exito", "Gasto aprobado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gastos";
    }

    @PostMapping("/gastos/{id}/rechazar")
    public String rechazarGasto(@PathVariable Long id, RedirectAttributes ra) {
        try {
            gastoService.rechazarGasto(id);
            ra.addFlashAttribute("exito", "Gasto rechazado.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/gastos";
    }

    @GetMapping("/registrar-gasto")
    public String registrarGastoForm(Model model, Authentication auth) {
        addNavModel(model, auth, "registrar-gasto");
        Usuario usuario = getUsuarioActual(auth);

        List<CajaChica> cajas = cajaChicaService.listar().stream()
            .filter(c -> usuario.getDepartamento() != null &&
                         c.getDepartamento() != null &&
                         c.getDepartamento().getId().equals(usuario.getDepartamento().getId()))
            .collect(Collectors.toList());

        List<PresupuestoArea> presupuestos = presupuestoAreaRepository.findAll().stream()
            .filter(p -> usuario.getDepartamento() != null &&
                         p.getDepartamento() != null &&
                         p.getDepartamento().getId().equals(usuario.getDepartamento().getId()))
            .collect(Collectors.toList());

        List<Gasto> misGastos = gastoService.listarPorUsuario(usuario.getId()).stream()
            .sorted((a, b) -> b.getFecha() != null && a.getFecha() != null
                ? b.getFecha().compareTo(a.getFecha()) : 0)
            .limit(5)
            .collect(Collectors.toList());

        model.addAttribute("areaNombre",
            usuario.getDepartamento() != null ? usuario.getDepartamento().getNombre() : "Sin área");
        model.addAttribute("cajas", cajas);
        model.addAttribute("presupuestos", presupuestos);
        model.addAttribute("misGastos", misGastos);
        return "registrar-gasto";
    }

    @PostMapping("/registrar-gasto")
    public String registrarGasto(@RequestParam Long cajaId,
                                  @RequestParam Long presupuestoId,
                                  @RequestParam String categoria,
                                  @RequestParam BigDecimal monto,
                                  @RequestParam String descripcion,
                                  @RequestParam(required = false) org.springframework.web.multipart.MultipartFile archivo,
                                  Authentication auth,
                                  RedirectAttributes ra) {
        try {
            Gasto gasto = new Gasto();
            gasto.setCategoria(categoria);
            gasto.setMonto(monto);
            gasto.setDescripcion(descripcion);
            Gasto guardado = gastoService.registrarGasto(gasto, cajaId, presupuestoId, auth.getName());

            if (archivo != null && !archivo.isEmpty()) {
                String tipo = archivo.getContentType();
                if (tipo != null && (tipo.equals("image/jpeg") || tipo.equals("image/png") || tipo.equals("application/pdf"))) {
                    java.io.File carpeta = new java.io.File("comprobantes/");
                    if (!carpeta.exists()) carpeta.mkdirs();
                    String nombreArchivo = java.util.UUID.randomUUID() + "_" + archivo.getOriginalFilename();
                    java.nio.file.Path ruta = java.nio.file.Paths.get("comprobantes/" + nombreArchivo);
                    java.nio.file.Files.write(ruta, archivo.getBytes());
                    gastoService.agregarComprobante(guardado.getId(), ruta.toString());
                } else {
                    ra.addFlashAttribute("exito", "Gasto registrado. Comprobante ignorado (formato no válido).");
                    return "redirect:/registrar-gasto";
                }
            }

            ra.addFlashAttribute("exito", "Gasto registrado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/registrar-gasto";
    }


    @GetMapping("/presupuesto")
    public String presupuesto(Model model, Authentication auth) {
        addNavModel(model, auth, "presupuesto");
        model.addAttribute("presupuestos", presupuestoAreaRepository.findAll());
        model.addAttribute("departamentos", departamentoRepository.findAll());
        return "presupuesto";
    }

    @PostMapping("/presupuesto/crear-departamento")
    public String crearDepartamento(@RequestParam String nombre, RedirectAttributes ra) {
        try {
            presupuestoAreaService.crearDepartamento(nombre);
            ra.addFlashAttribute("exitoDepto", "Departamento creado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorDepto", e.getMessage());
        }
        return "redirect:/presupuesto";
    }

    @PostMapping("/presupuesto/asignar")
    public String asignarPresupuesto(@RequestParam Long deptoId,
                                      @RequestParam String mes,
                                      @RequestParam BigDecimal presupuestoMensual,
                                      RedirectAttributes ra) {
        try {
            presupuestoAreaService.crearPresupuesto(deptoId, mes, presupuestoMensual);
            ra.addFlashAttribute("exitoPresupuesto", "Presupuesto asignado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorPresupuesto", e.getMessage());
        }
        return "redirect:/presupuesto";
    }

    @GetMapping("/usuarios")
    public String usuarios(Model model, Authentication auth) {
        addNavModel(model, auth, "usuarios");
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        model.addAttribute("departamentos", departamentoRepository.findAll());
        return "usuarios";
    }

    @PostMapping("/usuarios/registrar")
    public String registrarUsuario(@RequestParam String username,
                                    @RequestParam String password,
                                    @RequestParam String rol,
                                    @RequestParam(required = false) Long deptoId,
                                    RedirectAttributes ra) {
        try {
            Usuario u = new Usuario();
            u.setUsername(username);
            u.setPassword(password);
            u.setRol(rol);
            if (deptoId != null) {
                u.setDepartamento(departamentoRepository.findById(deptoId).orElse(null));
            }
            usuarioService.registrarUsuario(u);
            ra.addFlashAttribute("exito", "Usuario registrado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/usuarios/{id}/editar")
    public String editarUsuario(@PathVariable Long id,
                                 @RequestParam String username,
                                 @RequestParam(required = false) String password,
                                 @RequestParam String rol,
                                 @RequestParam(required = false) Long deptoId,
                                 RedirectAttributes ra) {
        try {
            Usuario u = new Usuario();
            u.setUsername(username);
            u.setPassword(password);
            u.setRol(rol);
            if (deptoId != null) {
                u.setDepartamento(departamentoRepository.findById(deptoId).orElse(null));
            }
            usuarioService.actualizarUsuario(id, u);
            ra.addFlashAttribute("exito", "Usuario actualizado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/usuarios/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Long id,
                                   Authentication auth,
                                   RedirectAttributes ra) {
        try {
            Usuario actual = getUsuarioActual(auth);
            if (actual.getId().equals(id)) {
                ra.addFlashAttribute("error", "No puedes eliminar tu propio usuario.");
            } else {
                usuarioService.eliminarUsuario(id);
                ra.addFlashAttribute("exito", "Usuario eliminado.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/notificaciones")
    public String notificaciones(Model model, Authentication auth) {
        addNavModel(model, auth, "notificaciones");
        Usuario usuario = getUsuarioActual(auth);
        model.addAttribute("notificaciones", notificacionService.listarPorUsuario(usuario.getId()));
        return "notificaciones";
    }

    @PostMapping("/notificaciones/{id}/leer")
    public String marcarLeida(@PathVariable Long id, RedirectAttributes ra) {
        try {
            notificacionService.marcarComoLeida(id);
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/notificaciones";
    }

    @PostMapping("/notificaciones/leer-todas")
    public String marcarTodasLeidas(Authentication auth, RedirectAttributes ra) {
        try {
            Usuario usuario = getUsuarioActual(auth);
            notificacionService.marcarTodasComoLeidas(usuario.getId());
            ra.addFlashAttribute("exito", "Todas las notificaciones fueron marcadas como leídas.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/notificaciones";
    }

    @GetMapping("/movimientos")
    public String movimientos(Model model, Authentication auth) {
        addNavModel(model, auth, "movimientos");
        boolean admin = esAdmin(auth);
        Usuario usuario = getUsuarioActual(auth);

        List<Movimiento> movimientos = movimientoRepository.findAll();
        if (!admin && usuario.getDepartamento() != null) {
            Long deptoId = usuario.getDepartamento().getId();
            movimientos = movimientos.stream()
                .filter(m -> m.getCajaChica() != null &&
                             m.getCajaChica().getDepartamento() != null &&
                             m.getCajaChica().getDepartamento().getId().equals(deptoId))
                .collect(Collectors.toList());
        }

        model.addAttribute("movimientos", movimientos);
        model.addAttribute("cajas", cajaChicaService.listar());
        return "movimientos";
    }

    @PostMapping("/movimientos/registrar")
    public String registrarMovimiento(@RequestParam Long cajaId,
                                       @RequestParam String tipo,
                                       @RequestParam BigDecimal monto,
                                       @RequestParam String descripcion,
                                       RedirectAttributes ra) {
        try {
            movimientoService.registrarMovimiento(cajaId, tipo, monto, descripcion);
            ra.addFlashAttribute("exito", "Movimiento registrado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/movimientos";
    }
}
