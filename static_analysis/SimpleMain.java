/* Soot - a J*va Optimization Framework
 * Copyright (C) 2008 Eric Bodden
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */
import java.util.Map;
import java.util.List;
import java.util.Iterator;

import soot.Body;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.util.Chain;
import soot.jimple.*;
import soot.shimple.*;
import soot.BodyTransformer;
import soot.G;
import soot.PackManager;
import soot.Transform;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.MHGPostDominatorsFinder;
import soot.tagkit.BytecodeOffsetTag;

public class SimpleMain {

  public static void main(String[] args) {
    // this jb pack does not work, perhaps, by design
    PackManager.v().getPack("jb").add(
        new Transform("jb.myTransform", new BodyTransformer() {
          protected void internalTransform(Body body, String phase, Map options) {
            SootMethod method = body.getMethod();
            Chain units = body.getUnits();
            Iterator it = units.snapshotIterator();
            while(it.hasNext()) 
              G.v().out.println("*it = "+it.next());
          }
        }));
    PackManager.v().getPack("stp").add(
        new Transform("stp.myTransform", new BodyTransformer() {
          protected void internalTransform(Body body, String phase, Map options) {
            // // prints out locals, but those dont have stack locations
            // Chain<Local> locals = body.getLocals();
            // G.v().out.println("locals = "+locals);
            // Iterator it = locals.iterator();
            // while(it.hasNext()) {
            //   Local l = (Local) it.next();
            //   G.v().out.println("l.name = " + l.getName() + 
            //     " l.type = " + l.getType() + 
            //     " l.num = " + l.getNumber() + 
            //     " l.getUB = " + l.getUseBoxes());
            // }
            MyAnalysis m = new MyAnalysis(new ExceptionalUnitGraph(body));
            // use G.v().out instead of System.out so that Soot can
            // redirect this output to the Eclipse console
            // if(!body.getMethod().getName().contains("testMe3")) return;
            // G.v().out.println(body.getMethod());
            // Iterator it = body.getUnits().iterator();
            // while (it.hasNext()) {
            //   Unit u = (Unit) it.next();
            //   SimpleStmtSwitch SimpleStmtSwitch = new SimpleStmtSwitch();
            //   u.apply(SimpleStmtSwitch);
            //   //G.v().out.println(u);
            //   G.v().out.println("");
            // }
          } 
        } 
        )
    );
    soot.Main.main(args);
  }
  
  public static class MyAnalysis /*extends ForwardFlowAnalysis */ {
    ExceptionalUnitGraph g;
    int index=0;
    int if_ret_distance = 0, if_ret_count = 0;
    int if_iv_distance = 0, if_iv_count = 0;
    int if_throw_distance = 0, if_throw_count = 0;
    public MyAnalysis(ExceptionalUnitGraph exceptionalUnitGraph) {
      g = exceptionalUnitGraph;
      doAnalysis();
    }
    
    private void printTags(Stmt stmt) {
      Iterator tags_it = stmt.getTags().iterator();
      while(tags_it.hasNext()) G.v().out.println(tags_it.next());
      G.v().out.println("  end tags");
    }
 
    public Unit getIPDom(Unit u) {
      MHGPostDominatorsFinder m = new MHGPostDominatorsFinder(g);
      Unit u_IPDom = (Unit) m.getImmediateDominator(u);
      return u_IPDom;
    }

    public void countDistanceToExitPoints(List<Unit> succs, Unit commonSucc, int ifIndex) {
      Unit thenUnit = succs.get(0);
      Unit elseUnit = succs.get(1);
      int savedIndex = index;

      // Create thenExpr
      while(thenUnit != commonSucc) {
				BytecodeOffsetTag b = (BytecodeOffsetTag) ((Stmt)thenUnit).getTag("BytecodeOffsetTag");
				if((b != null) && (b.getBytecodeOffset() != 0)) index++;
                
        SimpleStmtSwitch simpleStmtSwitch = new SimpleStmtSwitch();
        thenUnit.apply(simpleStmtSwitch);
        if(simpleStmtSwitch.isInvokeVirtual() || simpleStmtSwitch.isInvokeInterface()) {
          if_iv_distance += (index - savedIndex);
          if_iv_count++;
          if(savedIndex > ifIndex) {
            if_iv_distance += (index - ifIndex);
            if_iv_count++;
          }
        }
        if(simpleStmtSwitch.isReturn()) {
          if_ret_distance += (index - savedIndex);
          if_ret_count++;
          if(savedIndex > ifIndex) {
            if_ret_distance += (index - ifIndex);
            if_ret_count++;
          }
        }
        if(simpleStmtSwitch.isThrow()) {
          if_throw_distance += (index - savedIndex);
          if_throw_count++;
          if(savedIndex > ifIndex) {
            if_throw_distance += (index - ifIndex);
            if_throw_count++;
          }
        }
        List<Unit> s = g.getUnexceptionalSuccsOf(thenUnit);
        if(s.size() > 1) {
          Unit ipdom_thenUnit = getIPDom(thenUnit);
          countDistanceToExitPoints(s, ipdom_thenUnit, ifIndex);
          thenUnit = ipdom_thenUnit; 
        } else if(s.size() == 1) { 
          thenUnit = g.getUnexceptionalSuccsOf(thenUnit).get(0);
				} else break;
      }

      index = savedIndex;

      // Create elseExpr, similar to thenExpr
      while(elseUnit != commonSucc) {
				BytecodeOffsetTag b = (BytecodeOffsetTag) ((Stmt)elseUnit).getTag("BytecodeOffsetTag");
				if( (b != null) && (b.getBytecodeOffset() != 0)) index++;

        SimpleStmtSwitch simpleStmtSwitch= new SimpleStmtSwitch();
        elseUnit.apply(simpleStmtSwitch);
        if(simpleStmtSwitch.isInvokeVirtual() || simpleStmtSwitch.isInvokeInterface()) {
          if_iv_distance += (index - savedIndex);
          if_iv_count++;
          if(savedIndex > ifIndex) {
            if_iv_distance += (index - ifIndex);
            if_iv_count++;
          }
        }
        if(simpleStmtSwitch.isReturn()) {
          if_ret_distance += (index - savedIndex);
          if_ret_count++;
          if(savedIndex > ifIndex) {
            if_ret_distance += (index - ifIndex);
            if_ret_count++;
          }
        }
        if(simpleStmtSwitch.isThrow()) {
          if_throw_distance += (index - savedIndex);
          if_throw_count++;
          if(savedIndex > ifIndex) {
            if_throw_distance += (index - ifIndex);
            if_throw_count++;
          }
        }
        List<Unit> s = g.getUnexceptionalSuccsOf(elseUnit);
        if(s.size() > 1) {
          Unit ipdom_elseUnit = getIPDom(elseUnit);
          countDistanceToExitPoints(s, ipdom_elseUnit, ifIndex);
          elseUnit = ipdom_elseUnit; 
        } else if(s.size() == 1) 
          elseUnit = g.getUnexceptionalSuccsOf(elseUnit).get(0);
					else break;
      }
    }

    

    public void doAnalysis() {
      List<Unit> heads = g.getHeads(); 
      if(heads.size()==1) {
        Unit u = (Unit) heads.get(0);
        while(true) {
					if(u == null) break;
					BytecodeOffsetTag b = (BytecodeOffsetTag) ((Stmt)u).getTag("BytecodeOffsetTag");
					if((b != null) && (b.getBytecodeOffset() != 0)) index++;
          //printTags((Stmt)u);
          SimpleStmtSwitch simpleStmtSwitch = new SimpleStmtSwitch();
          u.apply(simpleStmtSwitch);
          List<Unit> succs = g.getUnexceptionalSuccsOf(u);
          if(succs.size()==1) {
            u = succs.get(0);
            continue;
          } else if (succs.size()==0) break;
          else {
            G.v().out.printf("  #succs = %d\n", succs.size());
            Unit commonSucc = getIPDom(u);
            countDistanceToExitPoints(succs, commonSucc, index);
            u = commonSucc;
          }
          G.v().out.println("");
        }
				G.v().out.println("if-iv-distance = " + if_iv_distance + " ("+if_iv_count + ")");
				G.v().out.println("if-ret-distance = " + if_ret_distance + " ("+if_ret_count + ")");
				G.v().out.println("if-throw-distance = " + if_throw_distance + " ("+if_throw_count + ")");
      }
    }

  }
  
}
