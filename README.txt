MIT TEAL/Studio Physics TEALsim @VERSION@
http://icampus.mit.edu/teal/content/?TEALsim

This release is a snapshot of the current state of the TEALsim software and is considered a "for comment" release. We include three example simulations. We expect to make a more formal and engineered release soon and would appreciate comments ranging from general comments on architecture to specific recomendations that would improve the future release.

Please send comments to: teal-comment@mit.edu.

------- Requirements

TEALsim v0.3 requires at least JDK 1.4.2. and the Java3D Extensions

------- The Distribution

In this distribution, there are two jar files, TEALsim-core.jar containing the TEALsim framework and TEALsim-simulations.jar a collection of simulations and tutorials that you can use as examples to jump start the development of your own simulations.

To run the examples from the distribution, use:

Two Point Charge simulation:
  java -cp TEALsim-simulations.jar -jar lib\TEALsim-core.jar -n tealsim.physics.em.PCharges

Falling Coil simulation:
  java -cp TEALsim-simulations.jar -jar lib\TEALsim-core.jar -n tealsim.physics.em.FallingCoil

Generating Plane Wave Radiation simulation:
  java -cp TEALsim-simulations.jar -jar lib\TEALsim-core.jar -n tealsim.physics.em.EMRadiatorApp

There is also a directory of JNLP files that may be used to launch individual examples.

------- Contributing

If you want to contribute to the project, see
http://icampus.mit.edu/teal/content/?TEALsim for directions.



