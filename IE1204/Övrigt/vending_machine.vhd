-- File:   vending_machine.vhd
--
-- Author: Ingo Sander
--
-- Date:   2010-09-30
--
-- Description:
--
-- Synthesizable description for the vending machine from lecture 10 of
-- the course IE1204 Digital Design (based on a similar machine presented in
-- the book of W. I. Fletcher: Engineering Approach to Digital Design)

LIBRARY IEEE;
USE IEEE.STD_LOGIC_1164.ALL;

ENTITY Vending_Machine IS
   PORT (
      -- Inputs
      coin_present  : IN std_logic;
      gt_1_euro     : IN std_logic;
      eq_1_euro     : IN std_logic;
      lt_1_euro     : IN std_logic;
      drop_ready    : IN std_logic;
      changer_ready : IN std_logic;
      reset_n       : IN std_logic;
      clk           : IN std_logic;

      -- Outputs
      dec_acc        : OUT std_logic;
      clr_acc        : OUT std_logic;
      drop           : OUT std_logic;
      return_10_cent : OUT std_logic);
END Vending_Machine;

ARCHITECTURE Moore_FSM OF Vending_Machine IS

   TYPE   state_type IS (a, b, c, d, e, f, g);
   -- We can use state encoding according to BV 8.4.6 to
   -- enforce a particular encoding
   -- ATTRIBUTE enum_encoding : string;
   -- ATTRIBUTE enum_encoding OF state_type : TYPE IS "000 001 011 110 111 100 101";
   SIGNAL current_state, next_state      : state_type;
BEGIN  -- Moore_FSM
   
   NEXTSTATE : PROCESS (current_state, coin_present, gt_1_euro, eq_1_euro, lt_1_euro, drop_ready, changer_ready)
   BEGIN  -- PROCESS NEXT_STATE
      CASE current_state IS
         WHEN a => IF coin_present = '1' THEN
                      next_state <= b;
                   ELSE
                      next_state <= a;
                   END IF;
         WHEN b => IF coin_present = '0' THEN
                      next_state <= c;
                   ELSE
                      next_state <= b;
                   END IF;
         WHEN c => IF LT_1_EURO = '1' THEN
                      next_state <= a;
                   ELSIF Eq_1_Euro = '1' THEN
                      next_state <= d;
                   ELSE
                      next_state <= f;
                   END IF;
         WHEN d => IF drop_ready = '1' THEN
                      next_state <= f;
                   ELSE
                      next_state <= d;
                   END IF;
         WHEN e => next_state <= a;
         WHEN f => IF changer_ready = '1' THEN
                      next_state <= g;
                   ELSE
                      next_state <= f;
                   END IF;
         WHEN g      => next_state <= c;
         WHEN OTHERS => next_state <= a;
      END CASE;
   END PROCESS NEXTSTATE;

   OUTPUT : PROCESS (current_state)
   BEGIN  -- PROCESS OUTPUT
      drop           <= '0';
      clr_acc        <= '0';
      dec_acc        <= '0';
      return_10_cent <= '0';
      CASE current_state IS
         WHEN d      => drop           <= '1';
         WHEN e      => clr_acc        <= '1';
         WHEN f      => return_10_cent <= '1';
         WHEN g      => dec_acc        <= '1';
         WHEN OTHERS => NULL;
      END CASE;
   END PROCESS OUTPUT;

   CLOCK : PROCESS (clk, reset_n)
   BEGIN  -- PROCESS CLOCK
      IF reset_n = '0' THEN  		  -- asynchronous reset (active low)
         current_state <= a;
      ELSIF clk'event AND clk = '1' THEN  -- rising clock edge
         current_state <= next_state;
      END IF;
   END PROCESS CLOCK;
   

END Moore_FSM;

